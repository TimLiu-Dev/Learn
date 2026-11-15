import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun countPrimes(from: Int, to: Int): Int {                 // same trial division as Sizing
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

// Amdahl's law: p = the fraction of the 1-thread time that can run in parallel
fun speedup(p: Double, n: Int): Double = 1 / ((1 - p) + p / n)

const val SIZE = 150_000
const val SERIAL_CHUNKS = 28
const val CHUNKS = 144

// Pretend chunks 0..27 can't be split (say, they parse the input). Only the rest is parallel.
fun serialPart(): Int = countPrimes(0, SERIAL_CHUNKS * SIZE)

fun parallelPart(threads: Int): Int =
    Executors.newFixedThreadPool(threads).use { pool ->
        val tasks = (SERIAL_CHUNKS until CHUNKS).map { i -> Callable { countPrimes(i * SIZE, (i + 1) * SIZE) } }
        pool.invokeAll(tasks).sumOf { it.get() }
    }

fun millis(work: () -> Int): Long {
    val start = System.nanoTime()
    work()
    return (System.nanoTime() - start) / 1_000_000
}

fun main() {
    val cores = Runtime.getRuntime().availableProcessors()
    millis { serialPart() + parallelPart(cores) }          // warm-up run, result ignored
    val serial = millis { serialPart() }
    val one = millis { serialPart() + parallelPart(1) }
    val p = 1 - serial.toDouble() / one
    println("1 thread: %d ms, of which %d ms is serial, so p = %.2f".format(one, serial, p))
    for (n in listOf(4, cores)) {
        val t = millis { serialPart() + parallelPart(n) }
        println("%2d threads: %4d ms  measured %4.1fx  Amdahl's limit %4.1fx".format(n, t, one.toDouble() / t, speedup(p, n)))
    }
    println("64 cores could give at most %.1fx; infinitely many, %.1fx".format(speedup(p, 64), 1 / (1 - p)))
}
