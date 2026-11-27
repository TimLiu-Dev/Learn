import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.*

// A callback-style client, shaped like OkHttp's enqueue or an Android SDK call.
interface Callback { fun onResponse(body: String); fun onFailure(e: Exception) }
fun interface Call { fun cancel() }

object OldHttp {
    private val timer = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "http").apply { isDaemon = true } }
    fun enqueue(url: String, ms: Long, cb: Callback): Call {
        val task = timer.schedule({
            if (url.endsWith("/fail")) cb.onFailure(IllegalStateException("503 from $url")) else cb.onResponse("body of $url")
        }, ms, TimeUnit.MILLISECONDS)
        return Call { task.cancel(false); println("  OldHttp: call to $url cancelled") }
    }
}

// The bridge: one suspend function around one callback.
suspend fun get(url: String, ms: Long): String = suspendCancellableCoroutine { cont ->
    val call = OldHttp.enqueue(url, ms, object : Callback {
        override fun onResponse(body: String) = cont.resume(body)          // continue with a value
        override fun onFailure(e: Exception) = cont.resumeWithException(e) // continue by throwing
    })
    cont.invokeOnCancellation { call.cancel() }   // the coroutine was cancelled: stop the real work
}

fun main() = runBlocking {
    println(get("/books", 50))
    try { get("/fail", 50) } catch (e: IllegalStateException) { println("caught: ${e.message}") }
    val r = withTimeoutOrNull(100) { get("/slow", 1_000) }
    println("with a 100 ms timeout: $r")
}
