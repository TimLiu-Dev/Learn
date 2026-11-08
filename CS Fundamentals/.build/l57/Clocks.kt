// Lamport clocks and vector clocks, run over the same script of events.

class Lamport {
    var time = 0L
        private set
    fun tick(): Long { time++; return time }                     // IR1: count every event
    fun receive(stamp: Long): Long {                             // IR2(b): jump past the stamp
        time = maxOf(time, stamp) + 1
        return time
    }
}

class Vector(n: Int) {
    private val v = IntArray(n)
    fun tick(me: Int): IntArray { v[me]++; return v.copyOf() }
    fun receive(me: Int, other: IntArray): IntArray {
        for (i in v.indices) v[i] = maxOf(v[i], other[i])       // learn what the sender knew
        return tick(me)
    }
}

enum class Order { BEFORE, AFTER, EQUAL, CONCURRENT }

fun compare(a: IntArray, b: IntArray): Order {
    val le = a.indices.all { a[it] <= b[it] }
    val ge = a.indices.all { a[it] >= b[it] }
    return when {
        le && ge -> Order.EQUAL
        le -> Order.BEFORE
        ge -> Order.AFTER
        else -> Order.CONCURRENT
    }
}

// One step of the script: process p does a local event, sends msg, or receives msg.
sealed interface Step { val p: Int; val name: String }
data class Local(override val p: Int, override val name: String) : Step
data class Send(override val p: Int, val msg: String) : Step { override val name = "send $msg" }
data class Recv(override val p: Int, val msg: String) : Step { override val name = "recv $msg" }

class Event(val id: String, val p: Int, val lamport: Long, val vector: IntArray)

fun IntArray.show() = joinToString(",", "[", "]")

fun main() {
    val script = listOf(
        Local(0, "a"), Send(0, "m1"),
        Local(1, "b"), Recv(1, "m1"), Send(1, "m2"),
        Local(2, "c"), Recv(2, "m2"),
        Local(0, "d"),
    )
    val lamports = List(3) { Lamport() }
    val vectors = List(3) { Vector(3) }
    val inFlight = mutableMapOf<String, Pair<Long, IntArray>>()  // message -> the stamps it carries
    val events = mutableListOf<Event>()

    for (s in script) {
        val (l, v) = when (s) {
            is Local -> lamports[s.p].tick() to vectors[s.p].tick(s.p)
            is Send -> (lamports[s.p].tick() to vectors[s.p].tick(s.p)).also { inFlight[s.msg] = it }
            is Recv -> inFlight.getValue(s.msg).let { (ml, mv) ->
                lamports[s.p].receive(ml) to vectors[s.p].receive(s.p, mv)
            }
        }
        events += Event("P${s.p + 1} ${s.name}", s.p, l, v)
    }

    println("== One run, two kinds of logical clock")
    println("%-12s %7s  %s".format("event", "Lamport", "vector"))
    for (e in events) println("%-12s %7d  %s".format(e.id, e.lamport, e.vector.show()))

    println()
    println("== What each clock can tell you about a pair of events")
    val byId = events.associateBy { it.id }
    val pairs = listOf("P1 send m1" to "P2 recv m1", "P1 a" to "P3 recv m2",
                       "P2 b" to "P1 d", "P1 d" to "P3 recv m2", "P1 a" to "P3 c")
    for ((x, y) in pairs) {
        val a = byId.getValue(x); val b = byId.getValue(y)
        val sign = if (a.lamport < b.lamport) "<" else if (a.lamport > b.lamport) ">" else "="
        println("%-10s vs %-10s  Lamport %d %s %d   vector %s".format(
            x, y, a.lamport, sign, b.lamport, compare(a.vector, b.vector)))
    }

    println()
    println("== A total order: sort by (Lamport time, process number)")
    println(events.sortedWith(compareBy({ it.lamport }, { it.p })).joinToString("  ") { "${it.id}@${it.lamport}" })

    println()
    println("== Version vectors on two cart replicas (entry 0 counts A's writes, entry 1 B's)")
    val base = intArrayOf(1, 0)
    val onA = intArrayOf(2, 0)                        // A adds lamp during the partition
    val onB = intArrayOf(1, 1)                        // B adds pen during the partition
    println("[book,lamp] ${onA.show()} vs [book,pen] ${onB.show()}: ${compare(onA, onB)} -> keep both, merge")
    val merged = IntArray(2) { maxOf(onA[it], onB[it]) }.also { it[1]++ }  // B writes the merge
    println("[book,lamp,pen] ${merged.show()} vs [book,lamp] ${onA.show()}: ${compare(merged, onA)} -> safe to overwrite")
    println("[book] ${base.show()} vs [book,lamp,pen] ${merged.show()}: ${compare(base, merged)} -> a stale write, ignore it")
}
