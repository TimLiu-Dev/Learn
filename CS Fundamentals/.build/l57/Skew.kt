import kotlin.time.TimeSource

// Each node reads its OWN clock: true time plus an error (offset) in ms.
// "True time" exists only inside this simulation; real nodes never see it.
class Node(val name: String, var offsetMs: Long) {
    fun now(trueMs: Long) = trueMs + offsetMs
}

data class Versioned(val value: String, val ts: Long, val node: String)

// A last-write-wins register: keep whichever version has the bigger timestamp.
class Replica(val node: Node) {
    var current: Versioned? = null
    fun write(value: String, trueMs: Long): Versioned {
        val v = Versioned(value, node.now(trueMs), node.name)
        receive(v)
        return v
    }
    fun receive(v: Versioned) {
        val mine = current
        if (mine == null || v.ts > mine.ts) current = v
    }
}

// Two edits 'gapMs' apart, the second on the replica whose clock is 'skewMs' slow.
fun lostUpdate(skewMs: Long, gapMs: Long): Boolean {
    val a = Replica(Node("A", 0))
    val b = Replica(Node("B", -skewMs))
    val first = a.write("Ana", 1_000)
    val second = b.write("Ana Lima", 1_000 + gapMs)
    a.receive(second); b.receive(first)                 // replicate both ways
    return a.current!!.value != "Ana Lima"
}

fun main() {
    println("== Last write wins with a clock that is 300 ms slow")
    val a = Replica(Node("A", 0))
    val b = Replica(Node("B", -300))
    val first = a.write("Ana", 1_000)
    println("true 1000 ms  user saves name=Ana      on A, A's clock says ${first.ts}")
    val second = b.write("Ana Lima", 1_100)
    println("true 1100 ms  user saves name=Ana Lima on B, B's clock says ${second.ts}")
    a.receive(second); b.receive(first)
    println("after replication: A=${a.current!!.value}  B=${b.current!!.value}")
    println("the later edit lost: ${second.ts} < ${first.ts}, so LWW kept the older value")

    println()
    println("== Lost? (rows: clock skew, columns: gap between the two edits)")
    val gaps = listOf(10L, 50L, 100L, 500L)
    println("skew   " + gaps.joinToString("") { "%7s".format("${it}ms") })
    for (skew in listOf(0L, 20L, 100L, 300L)) {
        println("%4dms ".format(skew) + gaps.joinToString("") { gap ->
            "%7s".format(if (lostUpdate(skew, gap)) "LOST" else "ok")
        })
    }

    println()
    println("== A 5 s timeout measured with the time-of-day clock, while NTP steps it")
    for (stepMs in listOf(-2_000L, +2_000L)) {
        val node = Node("C", 0)
        val start = node.now(0)
        var t = 0L
        while (true) {
            t += 100
            if (t == 1_000L) node.offsetMs += stepMs        // NTP corrects the clock at true 1 s
            if (node.now(t) - start >= 5_000) break
        }
        println("step %+d ms at 1 s: timeout fired after %.1f s of real time".format(stepMs, t / 1000.0))
    }

    println()
    println("== The fix for durations: a monotonic clock (real run)")
    val mark = TimeSource.Monotonic.markNow()
    val t0 = System.nanoTime()
    Thread.sleep(50)
    println("TimeSource.Monotonic: slept 50 ms, measured ${mark.elapsedNow().inWholeMilliseconds} ms")
    println("System.nanoTime():    slept 50 ms, measured ${(System.nanoTime() - t0) / 1_000_000} ms")
}
