import kotlin.time.measureTime

fun work(n: Int): Long {                     // some O(n^2) work, the same every time
    var total = 0L
    for (i in 0 until n)
        for (j in 0 until n) total += (i xor j) and 7
    return total
}

fun main() {
    var sink = 0L
    for (run in 1..8) {
        val d = measureTime { sink += work(3_000) }
        println("run $run: ${d.inWholeMicroseconds} µs")
    }
    println("(sink $sink)")
}
