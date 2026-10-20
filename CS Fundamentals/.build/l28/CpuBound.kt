import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun primesIn(from: Int, to: Int): Int {
    var count = 0
    for (n in maxOf(from, 2) until to) {
        var d = 2
        var prime = true
        while (d * d <= n) { if (n % d == 0) { prime = false; break }; d++ }
        if (prime) count++
    }
    return count
}

fun timed(name: String, make: () -> ExecutorService) {
    val start = System.nanoTime()
    val total = make().use { ex ->
        ex.invokeAll((0 until 144).map { i -> Callable { primesIn(i * 150_000, (i + 1) * 150_000) } })
            .sumOf { it.get() }
    }
    println("%-24s %5d ms  (%d primes)".format(name, (System.nanoTime() - start) / 1_000_000, total))
}

fun main() {
    val cores = Runtime.getRuntime().availableProcessors()
    repeat(2) {                                              // second round = warmed up
        timed("fixed pool of $cores") { Executors.newFixedThreadPool(cores) }
        timed("virtual thread per task") { Executors.newVirtualThreadPerTaskExecutor() }
    }
}
