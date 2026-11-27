import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.stream.IntStream

// Steps for n to reach 1 under the Collatz rule (n/2 if even, 3n+1 if odd).
fun steps(start: Int): Int {
    var n = start.toLong()
    var count = 0
    while (n != 1L) { n = if (n % 2 == 0L) n / 2 else 3 * n + 1; count++ }
    return count
}

class StepSum(private val lo: Int, private val hi: Int) : RecursiveTask<Long>() {
    override fun compute(): Long {
        if (hi - lo <= 10_000) {                         // threshold: about 10,000 numbers per leaf
            var sum = 0L
            for (i in lo until hi) sum += steps(i)
            return sum
        }
        val mid = (lo + hi) ushr 1
        val left = StepSum(lo, mid).also { it.fork() }
        return StepSum(mid, hi).compute() + left.join()
    }
}

fun time(label: String, block: () -> Long) {
    val t = System.nanoTime()
    val result = block()
    println("%-16s %d in %d ms".format(label, result, (System.nanoTime() - t) / 1_000_000))
}

fun main() {
    val n = 5_000_000
    repeat(2) { round ->                                 // round 0 is warm-up
        if (round == 1) println("--- measured")
        time("plain loop") { var s = 0L; for (i in 1 until n) s += steps(i); s }
        time("RecursiveTask") { ForkJoinPool.commonPool().invoke(StepSum(1, n)) }
        time("parallel stream") { IntStream.range(1, n).parallel().mapToLong { steps(it).toLong() }.sum() }
    }
}
