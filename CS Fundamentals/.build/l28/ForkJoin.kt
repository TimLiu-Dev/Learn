import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.atomic.AtomicInteger

fun isPrime(n: Int): Boolean {
    if (n < 2) return false
    var d = 2
    while (d * d <= n) { if (n % d == 0) return false; d++ }
    return true
}

// Count the primes in [lo, hi): split in half until a piece is at most `threshold` numbers.
class CountPrimes(
    private val lo: Int, private val hi: Int,
    private val threshold: Int, private val leaves: AtomicInteger,
) : RecursiveTask<Int>() {
    override fun compute(): Int {
        if (hi - lo <= threshold) {                       // small enough: a plain loop
            leaves.incrementAndGet()
            return (lo until hi).count(::isPrime)
        }
        val mid = (lo + hi) ushr 1
        val left = CountPrimes(lo, mid, threshold, leaves)
        left.fork()                                       // queue the left half for any worker
        val right = CountPrimes(mid, hi, threshold, leaves).compute()  // do the right half here
        return right + left.join()                        // then wait for the left half
    }
}

fun main() {
    val n = 10_000_000
    println("cores = ${Runtime.getRuntime().availableProcessors()}, " +
        "common pool parallelism = ${ForkJoinPool.getCommonPoolParallelism()}")
    repeat(2) { round ->                                  // round 0 is warm-up, not printed
        var t = System.nanoTime()
        val seq = (0 until n).count(::isPrime)
        if (round == 1) println("plain loop:          $seq primes, ${(System.nanoTime() - t) / 1_000_000} ms")
        for (threshold in listOf(5_000_000, 100_000, 1_000, 10)) {
            val leaves = AtomicInteger()
            t = System.nanoTime()
            val count = ForkJoinPool.commonPool().invoke(CountPrimes(0, n, threshold, leaves))
            val ms = (System.nanoTime() - t) / 1_000_000
            if (round == 1) println("threshold %9d: %d primes, %7d leaves, %4d ms".format(threshold, count, leaves.get(), ms))
        }
    }
}
