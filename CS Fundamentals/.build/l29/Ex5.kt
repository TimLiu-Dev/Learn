import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

// The callback API: answers after `ms`, on a timer thread.
object Api {
    private val timer = Executors.newSingleThreadScheduledExecutor { r -> Thread(r).apply { isDaemon = true } }
    fun fetch(url: String, ms: Long, done: (String) -> Unit): () -> Unit {
        val task = timer.schedule({ done("${url.length} bytes from $url") }, ms, TimeUnit.MILLISECONDS)
        return { task.cancel(false) }
    }
}

suspend fun fetch(url: String): String = suspendCancellableCoroutine { cont ->
    val cancel = Api.fetch(url, 100) { cont.resume(it) }
    cont.invokeOnCancellation { cancel() }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val start = System.nanoTime()
    val urls = produce(capacity = 4) { for (i in 1..20) send("/page/$i") }
    val results = Channel<String>(Channel.UNLIMITED)
    val workers = List(3) { w ->
        launch { for (url in urls) results.send("w$w: ${fetch(url)}") }
    }
    launch { workers.joinAll(); results.close() }        // close only after every worker is done
    var count = 0
    for (r in results) { count++; if (count <= 2) println(r) }
    println("$count results in ${(System.nanoTime() - start) / 1_000_000} ms")
}
