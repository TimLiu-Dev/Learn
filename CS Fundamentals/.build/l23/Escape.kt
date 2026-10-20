import java.lang.management.ManagementFactory

data class Point(val x: Int, val y: Int)

fun sumPoints(n: Int): Long {
    var s = 0L
    for (i in 0 until n) {
        val p = Point(i, i + 1)                // never leaves this function
        s += p.x + p.y
    }
    return s
}

fun main() {
    val mx = ManagementFactory.getThreadMXBean() as com.sun.management.ThreadMXBean
    var sink = 0L
    for (round in 1..5) {
        val before = mx.currentThreadAllocatedBytes
        sink += sumPoints(1_000_000)
        val after = mx.currentThreadAllocatedBytes
        println("round $round: ${"%,d".format(after - before)} bytes allocated")
    }
    println("sink=$sink")
}
