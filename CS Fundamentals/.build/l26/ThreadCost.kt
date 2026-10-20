import kotlin.concurrent.thread

fun main() {
    val n = 10_000
    repeat(3) { round ->
        val start = System.nanoTime()
        val threads = List(n) { thread { } }     // start 10,000 threads that do nothing
        threads.forEach { it.join() }
        val ms = (System.nanoTime() - start) / 1_000_000
        println("round ${round + 1}: $n threads started and joined in $ms ms (${ms * 1000 / n} µs each)")
    }
}
