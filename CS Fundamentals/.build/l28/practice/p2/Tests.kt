import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test

class BillTest {
    private val ada = { id: Int -> CompletableFuture.completedFuture(User(id, "Ada")) }
    private val twoOrders = { _: User -> CompletableFuture.completedFuture(listOf(500, 600)) }

    @Test fun `Ada pays her orders minus the discount`() {
        val result = bill(1, ada, twoOrders, { CompletableFuture.completedFuture(150) })
        assertEquals("Ada pays 950", result.join())
    }

    @Test fun `orders are fetched for the user that was found`() {
        val bo = { id: Int -> CompletableFuture.completedFuture(User(id, "Bo")) }
        val byId = { u: User -> CompletableFuture.completedFuture(if (u.id == 7) listOf(100) else listOf(999)) }
        assertEquals("Bo pays 100", bill(7, bo, byId, { CompletableFuture.completedFuture(0) }).join())
    }

    @Test fun `bill returns the pipeline at once, before the discount arrives`() {
        val pending = CompletableFuture<Int>()
        val caller = Executors.newSingleThreadExecutor()
        try {
            // A guard, not a timing test: correct code returns at once, blocking code would never return.
            val result = caller.submit(Callable { bill(1, ada, twoOrders, { pending }) }).get(2, TimeUnit.SECONDS)
            assertFalse("nothing can be finished while the discount is pending", result.isDone)
            pending.complete(50)
            assertEquals("Ada pays 1050", result.join())
        } catch (e: TimeoutException) {
            fail("bill() blocked: return the chain instead of calling join() or get() inside it")
        } finally {
            pending.complete(0)
            caller.shutdownNow()
        }
    }

    @Test fun `a failed stage becomes a message with the real cause`() {
        val down = { _: User -> CompletableFuture.failedFuture<List<Int>>(IllegalStateException("orders down")) }
        assertEquals("failed: orders down", bill(1, ada, down, { CompletableFuture.completedFuture(0) }).join())
    }

    @Test fun `a failed lookup never asks for orders`() {
        val calls = AtomicInteger()
        val missing = { id: Int -> CompletableFuture.failedFuture<User>(NoSuchElementException("no user $id")) }
        val counted = { _: User -> calls.incrementAndGet(); CompletableFuture.completedFuture(listOf(1)) }
        assertEquals("failed: no user 9", bill(9, missing, counted, { CompletableFuture.completedFuture(0) }).join())
        assertEquals("ordersFor calls", 0, calls.get())
    }
}
