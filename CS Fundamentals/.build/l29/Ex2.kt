import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

val Ui = Executors.newSingleThreadExecutor { r -> Thread(r, "ui") }.asCoroutineDispatcher()
val network = Dispatchers.IO.limitedParallelism(3)        // at most 3 downloads at once

val running = AtomicInteger(); val maxRunning = AtomicInteger()

fun download(id: Int): String {                           // blocking, 200 ms
    val now = running.incrementAndGet()
    maxRunning.accumulateAndGet(now, ::maxOf)
    Thread.sleep(200)
    running.decrementAndGet()
    return "file$id"
}

suspend fun loadAll(ids: List<Int>, onEach: suspend () -> Unit): List<String> =
    coroutineScope {
        ids.map { id -> async(network) { download(id).also { onEach() } } }.awaitAll()
    }

fun main() = runBlocking {
    val t0 = System.nanoTime()
    var done = 0                                 // only ever touched on "ui": confined
    val files = loadAll((1..9).toList()) {
        withContext(Ui) { done++; print("[${Thread.currentThread().name} $done/9] ") }
    }
    println()
    println("${files.size} files, max ${maxRunning.get()} at once, " +
            "${(System.nanoTime() - t0) / 1_000_000} ms")
    Ui.close()
}
