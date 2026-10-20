import java.io.IOException
import java.util.Random
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

const val N = 1_000
val rnd = Random(28)                                   // same "network" every run
val DELAY = IntArray(N)
val FAILS = BooleanArray(N)

fun initNetwork() {
    for (i in 0 until N) {
        DELAY[i] = 50 + rnd.nextInt(151)               // 50..200 ms
        FAILS[i] = rnd.nextInt(100) < 2                // about 2% fail
    }
}

// pretend to download page i; returns its size in bytes
fun fetch(i: Int): Int {
    Thread.sleep(DELAY[i].toLong())
    if (FAILS[i]) throw IOException("HTTP 503 for /page/$i")
    return 1_000 + i
}

data class Outcome(val bytes: Int?, val error: String?)
data class Report(val name: String, val ms: Long, val ok: Int, val failed: Int, val firstError: String)

fun summarise(name: String, start: Long, outcomes: List<Outcome>) = Report(
    name, (System.nanoTime() - start) / 1_000_000,
    outcomes.count { it.error == null }, outcomes.count { it.error != null },
    outcomes.firstOrNull { it.error != null }?.error ?: ""
)

fun tasks(count: Int) = (0 until count).map { id -> Callable { fetch(id) } }

// a Future's failure arrives wrapped in ExecutionException: unwrap it (L10)
fun drain(futures: List<Future<Int>>) = futures.map { f ->
    try { Outcome(f.get(), null) } catch (e: ExecutionException) { Outcome(null, e.cause?.message) }
}

fun sequential(count: Int): Report {
    val start = System.nanoTime()
    val out = (0 until count).map { i ->
        try { Outcome(fetch(i), null) } catch (e: IOException) { Outcome(null, e.message) }
    }
    return summarise("sequential (first $count)", start, out)
}

fun fixedPool(): Report {
    val start = System.nanoTime()
    return Executors.newFixedThreadPool(16).use { pool ->
        summarise("fixed pool of 16", start, drain(pool.invokeAll(tasks(N))))
    }
}

fun completable(): Report {
    val start = System.nanoTime()
    return Executors.newFixedThreadPool(100).use { pool ->
        val futures = (0 until N).map { id ->
            CompletableFuture.supplyAsync({ fetch(id) }, pool)        // no checked exceptions to wrap
                .handle { bytes, e -> if (e == null) Outcome(bytes, null) else Outcome(null, e.cause?.message) }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()
        summarise("CompletableFuture, 100", start, futures.map { it.join() })
    }
}

fun virtualThreads(): Report {
    val start = System.nanoTime()
    return Executors.newVirtualThreadPerTaskExecutor().use { vt ->
        summarise("virtual thread per task", start, drain(vt.invokeAll(tasks(N))))
    }
}

fun main() {
    initNetwork()
    val reports = listOf(sequential(100), fixedPool(), completable(), virtualThreads())
    println("%-26s %8s %5s %7s  %s".format("version", "time", "ok", "failed", "first failure"))
    for (r in reports) println("%-26s %6d ms %5d %7d  %s".format(r.name, r.ms, r.ok, r.failed, r.firstError))
    println("sequential, all $N: at least %.1f s (the sum of every delay)".format(DELAY.sum() / 1000.0))
}
