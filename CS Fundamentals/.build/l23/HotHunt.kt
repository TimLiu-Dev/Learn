import java.lang.management.ManagementFactory

fun collatzSteps(start: Long): Int {
    var n = start
    var steps = 0
    while (n != 1L) {
        n = if (n % 2 == 0L) n / 2 else 3 * n + 1
        steps++
    }
    return steps
}

fun main() {
    val uptime = ManagementFactory.getRuntimeMXBean()
    var sink = 0L
    for (batch in 1..8) {
        val t0 = System.nanoTime()
        for (n in 1L..2_000L) sink += collatzSteps(n)
        val micros = (System.nanoTime() - t0) / 1_000
        println("batch $batch ends at ${uptime.uptime.toString().padStart(3)} ms: ${micros.toString().padStart(5)} µs")
    }
    println("sink=$sink")
}
