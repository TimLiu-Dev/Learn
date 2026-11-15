import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await

// A Java client: every call starts work and returns a future at once.
fun interface PriceClient {
    fun price(item: String): CompletableFuture<Int>
}

suspend fun total(client: PriceClient, items: List<String>): Int {
    val futures = items.map { client.price(it) }   // every request is in flight now
    return futures.sumOf { it.await() }            // await suspends; it doesn't block the thread
}

// When the timeout cancels the coroutine, await() cancels the future it was waiting on.
suspend fun totalOrNull(client: PriceClient, items: List<String>, timeoutMs: Long): Int? =
    withTimeoutOrNull(timeoutMs) { total(client, items) }
