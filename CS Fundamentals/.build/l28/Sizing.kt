import java.util.concurrent.Callable
import java.util.concurrent.Executors

// CPU-bound work: count the primes in [from, to) by trial division
fun countPrimes(from: Int, to: Int): Int {
    var count = 0
    for (n in from until to) {
        if (n < 2) continue
        var prime = true
        var d = 2
        while (d * d <= n) {
            if (n % d == 0) { prime = false; break }
            d++
        }
        if (prime) count++
    }
    return count
}

fun timeWith(threads: Int): Pair<Int, Long> {
    val chunks = 144                                       // 144 tasks of 150,000 numbers each
    val size = 150_000
    val start = System.nanoTime()
    val total = Executors.newFixedThreadPool(threads).use { pool ->
        val tasks = (0 until chunks).map { i -> Callable { countPrimes(i * size, (i + 1) * size) } }
        pool.invokeAll(tasks).sumOf { it.get() }
    }
    return total to (System.nanoTime() - start) / 1_000_000
}

fun main() {
    val cores = Runtime.getRuntime().availableProcessors()
    println("availableProcessors = $cores")
    timeWith(cores)                                        // warm-up run, result ignored
    for (threads in listOf(1, 4, cores, cores * 4)) {
        val (primes, ms) = timeWith(threads)
        println("%3d threads: %6d ms   (%d primes)".format(threads, ms, primes))
    }
}
