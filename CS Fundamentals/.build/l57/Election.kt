import java.util.PriorityQueue
import java.util.Random

// Raft leader election (§5.1–5.2 of the paper), simulated in one JVM.
// Time is virtual: nothing sleeps, and one seed always replays the same history.

enum class Role { FOLLOWER, CANDIDATE, LEADER }

sealed interface Msg { val term: Long; val from: Int }
data class RequestVote(override val term: Long, override val from: Int) : Msg
data class VoteReply(override val term: Long, override val from: Int, val granted: Boolean) : Msg
data class Heartbeat(override val term: Long, override val from: Int) : Msg        // AppendEntries, no entries
data class HeartbeatReply(override val term: Long, override val from: Int) : Msg

class Sim(
    seed: Long,
    val n: Int = 5,
    val timeoutMin: Int = 150, val timeoutMax: Int = 300,   // election timeout range, ms
    val heartbeatMs: Int = 50,
    val buggyVoter: Boolean = false,                        // forgets whom it voted for
    val tracing: Boolean = true,
) {
    val rnd = Random(seed)
    var now = 0L
        private set
    private var seq = 0L
    private class Ev(val at: Long, val seq: Long, val action: () -> Unit)
    private val queue = PriorityQueue<Ev>(compareBy<Ev>({ it.at }, { it.seq }))

    inner class Node(val id: Int) {
        var role = Role.FOLLOWER
        var term = 0L                    // currentTerm
        var votedFor: Int? = null        // votedFor, for the current term only
        val votes = mutableSetOf<Int>()
        var up = true
        var timerEpoch = 0               // bumping it cancels the pending election timer
        val name get() = "n${id + 1}"
    }

    val nodes = List(n) { Node(it) }
    private val cut = mutableSetOf<Pair<Int, Int>>()        // links that drop every message
    val leadersByTerm = sortedMapOf<Long, MutableSet<Int>>()   // for the safety check
    val trace = mutableListOf<String>()

    private fun log(text: String) { if (tracing) trace += "%6d ms  %s".format(now, text) }
    private fun schedule(delayMs: Long, action: () -> Unit) { queue += Ev(now + delayMs, seq++, action) }
    private fun linkUp(a: Int, b: Int) = (a to b) !in cut

    private fun send(from: Int, to: Int, msg: Msg) {
        if (!linkUp(from, to)) return
        schedule(2L + rnd.nextInt(9)) {                    // 2–10 ms on the wire
            if (linkUp(from, to) && nodes[to].up) deliver(nodes[to], msg)
        }
    }

    private fun resetElectionTimer(node: Node) {
        val epoch = ++node.timerEpoch
        val timeout = timeoutMin + rnd.nextInt(timeoutMax - timeoutMin + 1)
        schedule(timeout.toLong()) {
            if (node.up && node.timerEpoch == epoch && node.role != Role.LEADER) startElection(node)
        }
    }

    private fun startElection(node: Node) {
        node.role = Role.CANDIDATE
        node.term++
        node.votedFor = node.id
        node.votes.clear(); node.votes += node.id
        log("${node.name} times out: candidate for term ${node.term}")
        resetElectionTimer(node)                           // a split vote retries after a new timeout
        for (peer in nodes) if (peer.id != node.id) send(node.id, peer.id, RequestVote(node.term, node.id))
    }

    private fun deliver(node: Node, msg: Msg) {
        if (msg.term > node.term) {                        // any newer term: adopt it, become a follower
            if (node.role != Role.FOLLOWER) log("${node.name} steps down: saw term ${msg.term}")
            node.term = msg.term; node.votedFor = null; node.role = Role.FOLLOWER
        }
        when (msg) {
            is RequestVote -> {
                val free = node.votedFor == null || node.votedFor == msg.from || buggyVoter
                val grant = msg.term == node.term && free
                if (grant) {
                    node.votedFor = msg.from
                    resetElectionTimer(node)
                    log("${node.name} votes for n${msg.from + 1} in term ${msg.term}")
                }
                send(node.id, msg.from, VoteReply(node.term, node.id, grant))
            }
            is VoteReply -> if (node.role == Role.CANDIDATE && msg.term == node.term && msg.granted) {
                node.votes += msg.from
                if (node.votes.size > n / 2) becomeLeader(node)
            }
            is Heartbeat -> {
                if (msg.term == node.term) {
                    if (node.role == Role.CANDIDATE) log("${node.name} hears leader n${msg.from + 1}: back to follower")
                    node.role = Role.FOLLOWER
                    resetElectionTimer(node)
                }
                send(node.id, msg.from, HeartbeatReply(node.term, node.id))   // a stale leader learns the new term
            }
            is HeartbeatReply -> {}                        // the term check above already did the work
        }
    }

    private fun becomeLeader(node: Node) {
        node.role = Role.LEADER
        leadersByTerm.getOrPut(node.term) { mutableSetOf() } += node.id
        log("${node.name} is LEADER of term ${node.term}, votes from " +
            node.votes.sorted().joinToString(", ") { "n${it + 1}" })
        heartbeats(node, node.term)
    }

    private fun heartbeats(node: Node, term: Long) {
        if (!node.up || node.role != Role.LEADER || node.term != term) return
        for (peer in nodes) if (peer.id != node.id) send(node.id, peer.id, Heartbeat(term, node.id))
        schedule(heartbeatMs.toLong()) { heartbeats(node, term) }
    }

    fun start() { for (node in nodes) resetElectionTimer(node) }
    fun crash(id: Int) { nodes[id].up = false; log("${nodes[id].name} CRASHES") }
    fun restart(id: Int) {                                 // term and votedFor survive: Raft persists them
        val node = nodes[id]
        node.up = true; node.role = Role.FOLLOWER
        log("${node.name} restarts as a follower, still in term ${node.term}")
        resetElectionTimer(node)
    }
    fun partition(side: Set<Int>) {
        for (a in 0 until n) for (b in 0 until n) if ((a in side) != (b in side)) cut += a to b
        log("PARTITION " + side.sorted().joinToString(",", "{", "}") { "n${it + 1}" } + " | " +
            (0 until n).filter { it !in side }.joinToString(",", "{", "}") { "n${it + 1}" })
    }
    fun heal() { cut.clear(); log("partition HEALS") }
    fun at(timeMs: Long, action: () -> Unit) = schedule(timeMs - now, action)
    fun runUntil(timeMs: Long) {
        while (queue.isNotEmpty() && queue.peek().at <= timeMs) {
            val ev = queue.poll(); now = ev.at; ev.action()
        }
        now = timeMs
    }
    fun leader(): Node? = nodes.firstOrNull { it.up && it.role == Role.LEADER }
}

// Random crashes, restarts, partitions and heals every 300 ms for 3 s; count terms by number of leaders.
fun sweep(seeds: Int, buggy: Boolean): IntArray {
    val counts = IntArray(3)                               // terms with 0, 1, 2+ leaders
    var reported = false
    for (seed in 1..seeds) {
        val sim = Sim(seed.toLong(), buggyVoter = buggy, tracing = false)
        sim.start()
        for (t in 300L..3_000L step 300) {
            sim.runUntil(t)
            val r = sim.rnd.nextInt(4)
            val victim = sim.rnd.nextInt(sim.n)
            when (r) {
                0 -> if (sim.nodes.count { !it.up } < 2) sim.crash(victim)
                1 -> if (!sim.nodes[victim].up) sim.restart(victim)
                2 -> sim.partition((0 until sim.n).filter { sim.rnd.nextBoolean() }.toSet())
                else -> sim.heal()
            }
        }
        sim.runUntil(3_500)
        val maxTerm = sim.nodes.maxOf { it.term }
        for (term in 1..maxTerm) {
            val k = sim.leadersByTerm[term]?.size ?: 0
            counts[minOf(k, 2)]++
            if (k >= 2 && !reported) {
                reported = true
                println("  first violation: seed $seed, term $term, leaders " +
                    sim.leadersByTerm.getValue(term).sorted().joinToString(" and ") { "n${it + 1}" })
            }
        }
    }
    return counts
}

fun main() {
    println("== Seed 42: start, crash the leader at 600 ms, restart it at 1,200 ms")
    val sim = Sim(42)
    sim.start()
    sim.runUntil(600)
    val first = sim.leader()!!
    sim.crash(first.id)
    sim.at(1_200) { sim.restart(first.id) }
    sim.runUntil(1_500)
    sim.trace.forEach(::println)
    println("leaders by term: " + sim.leadersByTerm.entries.joinToString(", ") { (t, ids) ->
        "term $t -> " + ids.joinToString { "n${it + 1}" } })
    println("final terms: " + sim.nodes.joinToString(" ") { "${it.name}=${it.term}" })

    println()
    println("== Safety sweep: 2,000 seeds, random crashes and partitions for 3 s each")
    val ok = sweep(2_000, buggy = false)
    println("  terms with no leader: ${ok[0]}, one leader: ${ok[1]}, two or more leaders: ${ok[2]}")

    println()
    println("== The same sweep with a buggy voter that forgets whom it voted for")
    val bad = sweep(2_000, buggy = true)
    println("  terms with no leader: ${bad[0]}, one leader: ${bad[1]}, two or more leaders: ${bad[2]}")
}
