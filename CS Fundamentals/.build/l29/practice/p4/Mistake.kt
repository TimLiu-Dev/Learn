import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.*

fun interface PriceClient {
    fun price(item: String): CompletableFuture<Int>
}

// join() blocks the thread: the coroutine can't be cancelled while it waits.
suspend fun total(client: PriceClient, items: List<String>): Int {
    val futures = items.map { client.price(it) }
    return futures.sumOf { it.join() }
}

suspend fun totalOrNull(client: PriceClient, items: List<String>, timeoutMs: Long): Int? =
    withTimeoutOrNull(timeoutMs) { total(client, items) }
