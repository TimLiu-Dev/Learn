import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await

// A Java client: every call starts work and returns a future at once.
fun interface PriceClient {
    fun price(item: String): CompletableFuture<Int>
}

// Ask for every price, then suspend until all have arrived.
suspend fun total(client: PriceClient, items: List<String>): Int =
    TODO("start all the futures, then await() each one")

// Like total, but null after timeoutMs, with the pending futures cancelled.
suspend fun totalOrNull(client: PriceClient, items: List<String>, timeoutMs: Long): Int? =
    TODO("wrap total in a timeout that returns null")
