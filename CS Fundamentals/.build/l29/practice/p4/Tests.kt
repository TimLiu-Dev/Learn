import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Answers instantly, from a price list.
class InstantClient(private val prices: Map<String, Int>) : PriceClient {
    override fun price(item: String): CompletableFuture<Int> = CompletableFuture.completedFuture(prices.getValue(item))
}

// Like a batch server: no price is answered until every item has been asked for.
// A future still waiting after half a second completes with -1000.
class BatchClient(private val prices: Map<String, Int>) : PriceClient {
    private val pending = mutableListOf<Pair<String, CompletableFuture<Int>>>()

    override fun price(item: String): CompletableFuture<Int> {
        val f = CompletableFuture<Int>().completeOnTimeout(-1000, 500, TimeUnit.MILLISECONDS)
        pending += item to f
        if (pending.size == prices.size) pending.forEach { (i, fu) -> fu.complete(prices.getValue(i)) }
        return f
    }
}

// "durian" is slow: it answers 99 only after a second.
class SlowDurianClient : PriceClient {
    val futures = mutableListOf<CompletableFuture<Int>>()

    override fun price(item: String): CompletableFuture<Int> {
        val f = if (item == "durian") CompletableFuture<Int>().completeOnTimeout(99, 1, TimeUnit.SECONDS)
                else CompletableFuture.completedFuture(10)
        futures += f
        return f
    }
}

val prices = mapOf("apple" to 3, "kiwi" to 5, "fig" to 7)

class FutureBridgeTest {
    @Test fun `total adds up every price`() = runBlocking {
        assertEquals(15, total(InstantClient(prices), listOf("apple", "kiwi", "fig")))
    }

    @Test fun `total asks for every price before waiting for any`() = runBlocking {
        assertEquals("a negative total means you waited for one price before asking for the next",
            15, total(BatchClient(prices), listOf("apple", "kiwi", "fig")))
    }

    @Test fun `totalOrNull returns the total when it is in time`() = runBlocking {
        assertEquals(15, totalOrNull(InstantClient(prices), listOf("apple", "kiwi", "fig"), 500))
    }

    @Test fun `totalOrNull gives up with null`() = runBlocking {
        assertNull("the slow price takes a second, the timeout is 100 ms",
            totalOrNull(SlowDurianClient(), listOf("apple", "durian"), 100))
    }

    @Test fun `giving up cancels the pending future`() = runBlocking {
        val client = SlowDurianClient()
        totalOrNull(client, listOf("apple", "durian"), 100)
        assertTrue("the durian future should be cancelled, not left running", client.futures[1].isCancelled)
    }
}
