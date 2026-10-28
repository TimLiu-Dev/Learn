import java.io.IOException
import java.util.Random
import kotlin.math.min

// Exercise solution: retry with capped exponential backoff and full jitter.
// Only exceptions that `retryable` accepts are retried; anything else fails at once.
fun <T> retry(
    maxAttempts: Int, baseMs: Long, capMs: Long, random: Random,
    retryable: (Exception) -> Boolean, block: (attempt: Int) -> T,
): T {
    var attempt = 1
    while (true) {
        try {
            return block(attempt)
        } catch (e: Exception) {
            if (!retryable(e) || attempt == maxAttempts) throw e
            val backoff = min(capMs, baseMs shl (attempt - 1))
            val sleep = (random.nextDouble() * backoff).toLong()            // full jitter: 0..backoff
            println("  attempt $attempt failed (${e.message}); sleeping $sleep ms of up to $backoff")
            Thread.sleep(sleep)
            attempt++
        }
    }
}

fun main() {
    val random = Random(43)
    println("flaky GET, fails twice:")
    val price = retry(4, 100, 1000, random, { it is IOException }) { n -> if (n <= 2) throw IOException("503") else "12.99" }
    println("  -> $price")

    println("bad request, not retryable:")
    try { retry(4, 100, 1000, random, { it is IOException }) { throw IllegalArgumentException("400 bad id") } }
    catch (e: IllegalArgumentException) { println("  -> gave up at once: ${e.message}") }

    println("always down, 4 attempts:")
    try { retry(4, 100, 1000, random, { it is IOException }) { throw IOException("connection refused") } }
    catch (e: IOException) { println("  -> gave up: ${e.message}") }
}
