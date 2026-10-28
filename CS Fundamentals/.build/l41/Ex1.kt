import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

// Exercise 1: a service where 5% of calls take 50 ms. Measure its percentiles,
// then add hedging: if no answer after 5 ms, send a second copy and take whichever wins.
val calls = AtomicInteger()

fun call(): String {
    calls.incrementAndGet()
    val slow = ThreadLocalRandom.current().nextInt(100) < 5
    Thread.sleep(if (slow) 50 else 1)
    return "ok"
}

fun hedged(pool: ExecutorService): String {
    val first = CompletableFuture.supplyAsync(::call, pool)
    return try {
        first.get(5, TimeUnit.MILLISECONDS)
    } catch (slowSoFar: TimeoutException) {
        val second = CompletableFuture.supplyAsync(::call, pool)
        CompletableFuture.anyOf(first, second).join() as String     // first answer wins
    }
}

fun main() {
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        measure("plain") { pool.submit(::call).get() }
        measure("hedged") { hedged(pool) }
    }
}

fun measure(name: String, request: () -> String) {
    calls.set(0)
    val ms = DoubleArray(1_000) {
        val t = System.nanoTime()
        request()
        (System.nanoTime() - t) / 1e6
    }.sortedArray()
    println("%-7s p50 %5.1f ms  p95 %5.1f ms  p99 %5.1f ms  (%d calls for 1000 requests)"
        .format(name, ms[499], ms[949], ms[989], calls.get()))
}
