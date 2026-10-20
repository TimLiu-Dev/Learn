fun work(n: Int): Long {
    var s = 0L
    for (i in 0 until n) {
        s += (i xor (i ushr 3)) % 7
    }
    return s
}

fun main() {
    var sink = 0L
    for (batch in 1..10) {
        val t0 = System.nanoTime()
        repeat(20) { sink += work(10_000) }
        val micros = (System.nanoTime() - t0) / 1_000
        println("batch $batch: ${micros.toString().padStart(6)} µs")
    }
    println("sink=$sink")                       // use the result (L16)
}
