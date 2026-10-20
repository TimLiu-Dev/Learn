import kotlin.math.ln

var x = Math.PI
fun wrong() { ln(x) }                                // result thrown away
fun right(): Double = ln(x)                          // result returned and used

fun main() {
    var sink = 0.0
    for (round in 1..5) {
        val t0 = System.nanoTime()
        repeat(10_000_000) { wrong() }
        val t1 = System.nanoTime()
        repeat(10_000_000) { sink += right() }
        val t2 = System.nanoTime()
        println("round $round: discarded ${((t1 - t0) / 1_000).toString().padStart(5)} µs, " +
                "used ${((t2 - t1) / 1_000).toString().padStart(6)} µs")
    }
    println("sink=$sink")
}
