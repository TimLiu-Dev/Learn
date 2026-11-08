package raft

import java.util.PriorityQueue
import java.util.Random

// Raft (election + log replication, §5 of the paper) in a deterministic simulation.
// Left out: persistence to disk, snapshots and membership changes.

enum class Role { FOLLOWER, CANDIDATE, LEADER }

data class Entry(val term: Long, val command: String)

sealed interface Msg { val term: Long; val from: Int }
data class RequestVote(override val term: Long, override val from: Int, val lastIndex: Int, val lastTerm: Long) : Msg
data class VoteReply(override val term: Long, override val from: Int, val granted: Boolean) : Msg
data class AppendEntries(
    override val term: Long, override val from: Int,
    val prevIndex: Int, val prevTerm: Long, val entries: List<Entry>, val leaderCommit: Int,
) : Msg
data class AppendReply(override val term: Long, override val from: Int, val success: Boolean, val matchIndex: Int) : Msg

/** One line of history, plus who sent the message that caused it (for the widget). */
data class Event(val at: Long, val text: String, val from: Int? = null, val to: Int? = null)

class Sim(seed: Long, val n: Int = 5, val heartbeatMs: Int = 50) {
    val rnd = Random(seed)
    var now = 0L
        private set
    private var seq = 0L
    private class Ev(val at: Long, val seq: Long, val action: () -> Unit)
    private val queue = PriorityQueue<Ev>(compareBy<Ev>({ it.at }, { it.seq }))

    inner class Node(val id: Int) {
        val name = "n${id + 1}"
        var role = Role.FOLLOWER
        var term = 0L                           // persistent: currentTerm
        var votedFor: Int? = null               // persistent: votedFor
        val log = mutableListOf<Entry>()        // persistent: log[i - 1] holds index i
        var commitIndex = 0                     // volatile
        val applied = mutableListOf<String>()   // the state machine: commands applied, in order
        val votes = mutableSetOf<Int>()
        val nextIndex = IntArray(n)             // leader only
        val matchIndex = IntArray(n)            // leader only
        var up = true
        var timerEpoch = 0

        fun lastIndex() = log.size
        fun lastTerm() = log.lastOrNull()?.term ?: 0L
        fun termAt(index: Int) = if (index == 0) 0L else log[index - 1].term
        /** The election restriction (§5.4.1): is a candidate's log at least as up-to-date as mine? */
        fun isUpToDate(lastIndex: Int, lastTerm: Long) =
            lastTerm > lastTerm() || (lastTerm == lastTerm() && lastIndex >= lastIndex())
    }

    val nodes = List(n) { Node(it) }
    private val cut = mutableSetOf<Pair<Int, Int>>()
    var side: Set<Int>? = null                  // one side of the current partition, if any
        private set
    val leadersByTerm = sortedMapOf<Long, MutableSet<Int>>()
    val history = mutableListOf<Event>()
    var onEvent: (Event) -> Unit = {}

    private fun log(text: String, from: Int? = null, to: Int? = null) {
        val e = Event(now, text, from, to)
        history += e
        onEvent(e)
    }
    private fun schedule(delayMs: Long, action: () -> Unit) { queue += Ev(now + delayMs, seq++, action) }
    private fun linkUp(a: Int, b: Int) = (a to b) !in cut

    private fun send(from: Int, to: Int, msg: Msg) {
        if (!linkUp(from, to)) return
        schedule(2L + rnd.nextInt(9)) { if (linkUp(from, to) && nodes[to].up) deliver(nodes[to], msg) }
    }

    private fun resetElectionTimer(node: Node) {
        val epoch = ++node.timerEpoch
        schedule(150L + rnd.nextInt(151)) {
            if (node.up && node.timerEpoch == epoch && node.role != Role.LEADER) startElection(node)
        }
    }

    private fun startElection(node: Node) {
        node.role = Role.CANDIDATE
        node.term++
        node.votedFor = node.id
        node.votes.clear(); node.votes += node.id
        log("${node.name} times out: candidate for term ${node.term}")
        resetElectionTimer(node)
        val rv = RequestVote(node.term, node.id, node.lastIndex(), node.lastTerm())
        for (peer in nodes) if (peer.id != node.id) send(node.id, peer.id, rv)
    }

    private fun deliver(node: Node, msg: Msg) {
        if (msg.term > node.term) {
            if (node.role != Role.FOLLOWER) log("${node.name} steps down: saw term ${msg.term}", msg.from, node.id)
            node.term = msg.term; node.votedFor = null; node.role = Role.FOLLOWER
        }
        when (msg) {
            is RequestVote -> {
                val grant = msg.term == node.term &&
                    (node.votedFor == null || node.votedFor == msg.from) &&
                    node.isUpToDate(msg.lastIndex, msg.lastTerm)
                if (grant) {
                    node.votedFor = msg.from
                    resetElectionTimer(node)
                    log("${node.name} votes for n${msg.from + 1} in term ${msg.term}", node.id, msg.from)
                }
                send(node.id, msg.from, VoteReply(node.term, node.id, grant))
            }
            is VoteReply -> if (node.role == Role.CANDIDATE && msg.term == node.term && msg.granted) {
                node.votes += msg.from
                if (node.votes.size > n / 2) becomeLeader(node)
            }
            is AppendEntries -> onAppendEntries(node, msg)
            is AppendReply -> if (node.role == Role.LEADER && msg.term == node.term) onAppendReply(node, msg)
        }
    }

    private fun onAppendEntries(node: Node, msg: AppendEntries) {
        if (msg.term < node.term) { send(node.id, msg.from, AppendReply(node.term, node.id, false, 0)); return }
        if (node.role == Role.CANDIDATE) log("${node.name} hears leader n${msg.from + 1}: back to follower", msg.from, node.id)
        node.role = Role.FOLLOWER
        resetElectionTimer(node)
        // Consistency check: my log must hold the entry just before the new ones.
        if (msg.prevIndex > node.lastIndex() || node.termAt(msg.prevIndex) != msg.prevTerm) {
            send(node.id, msg.from, AppendReply(node.term, node.id, false, 0)); return
        }
        var index = msg.prevIndex
        for (entry in msg.entries) {
            index++
            if (index <= node.lastIndex() && node.termAt(index) != entry.term) {
                val dropped = node.log.subList(index - 1, node.log.size).joinToString { it.command }
                while (node.log.size >= index) node.log.removeAt(node.log.size - 1)
                log("${node.name} drops uncommitted [$dropped]: conflicts with the leader", msg.from, node.id)
            }
            if (index > node.lastIndex()) node.log += entry
        }
        if (msg.leaderCommit > node.commitIndex) {
            node.commitIndex = minOf(msg.leaderCommit, msg.prevIndex + msg.entries.size)
            apply(node)
        }
        send(node.id, msg.from, AppendReply(node.term, node.id, true, msg.prevIndex + msg.entries.size))
    }

    private fun onAppendReply(leader: Node, msg: AppendReply) {
        if (!msg.success) { leader.nextIndex[msg.from] = maxOf(1, leader.nextIndex[msg.from] - 1); return }
        leader.matchIndex[msg.from] = maxOf(leader.matchIndex[msg.from], msg.matchIndex)
        leader.nextIndex[msg.from] = leader.matchIndex[msg.from] + 1
        // Commit the highest index stored on a majority, but only by counting entries of the current term (§5.4.2).
        for (index in leader.lastIndex() downTo leader.commitIndex + 1) {
            val copies = leader.matchIndex.count { it >= index }
            if (leader.termAt(index) == leader.term && copies > n / 2) {
                leader.commitIndex = index
                log("${leader.name} commits up to index $index (${leader.log[index - 1].command}), stored on $copies of $n")
                apply(leader)
                break
            }
        }
    }

    private fun apply(node: Node) {
        while (node.applied.size < node.commitIndex) node.applied += node.log[node.applied.size].command
    }

    private fun becomeLeader(node: Node) {
        node.role = Role.LEADER
        leadersByTerm.getOrPut(node.term) { mutableSetOf() } += node.id
        node.nextIndex.fill(node.lastIndex() + 1)
        node.matchIndex.fill(0)
        node.matchIndex[node.id] = node.lastIndex()
        log("${node.name} is LEADER of term ${node.term}, votes from " +
            node.votes.sorted().joinToString(", ") { "n${it + 1}" })
        heartbeats(node, node.term)
    }

    // Each heartbeat is an AppendEntries carrying whatever that follower is missing.
    private fun heartbeats(node: Node, term: Long) {
        if (!node.up || node.role != Role.LEADER || node.term != term) return
        for (peer in nodes) if (peer.id != node.id) {
            val prev = node.nextIndex[peer.id] - 1
            val entries = node.log.subList(prev, node.log.size).toList()
            send(node.id, peer.id, AppendEntries(term, node.id, prev, node.termAt(prev), entries, node.commitIndex))
        }
        schedule(heartbeatMs.toLong()) { heartbeats(node, term) }
    }

    /** A client asks [id] to run [command]. Only a leader accepts; acceptance is not commitment. */
    fun propose(id: Int, command: String): Boolean {
        val node = nodes[id]
        if (!node.up || node.role != Role.LEADER) return false
        node.log += Entry(node.term, command)
        node.matchIndex[node.id] = node.lastIndex()
        log("client -> ${node.name}: $command (index ${node.lastIndex()}, term ${node.term}, not committed yet)")
        return true
    }

    fun start() { for (node in nodes) resetElectionTimer(node) }
    fun crash(id: Int) { nodes[id].up = false; log("${nodes[id].name} CRASHES") }
    fun restart(id: Int) {                       // term, votedFor and log are persistent; the rest is rebuilt
        val node = nodes[id]
        node.up = true; node.role = Role.FOLLOWER
        node.commitIndex = 0; node.applied.clear()
        log("${node.name} restarts")
        resetElectionTimer(node)
    }
    fun partition(side: Set<Int>) {
        for (a in 0 until n) for (b in 0 until n) if ((a in side) != (b in side)) cut += a to b
        this.side = side
        log("PARTITION " + group(side) + " | " + group((0 until n).filter { it !in side }.toSet()))
    }
    fun heal() { cut.clear(); side = null; log("partition HEALS") }
    fun at(timeMs: Long, action: () -> Unit) = schedule(timeMs - now, action)
    fun runUntil(timeMs: Long) {
        while (queue.isNotEmpty() && queue.peek().at <= timeMs) {
            val ev = queue.poll(); now = ev.at; ev.action()
        }
        now = timeMs
    }
    fun leader(): Node? = nodes.filter { it.up && it.role == Role.LEADER }.maxByOrNull { it.term }

    /** Election Safety: at most one leader per term. */
    fun electionSafe() = leadersByTerm.values.all { it.size <= 1 }
    /** State Machine Safety: no two nodes applied different commands at the same index. */
    fun stateMachineSafe() = nodes.all { a -> nodes.all { b ->
        (0 until minOf(a.applied.size, b.applied.size)).all { a.applied[it] == b.applied[it] } } }

    private fun group(ids: Set<Int>) = ids.sorted().joinToString(",", "{", "}") { "n${it + 1}" }
}
