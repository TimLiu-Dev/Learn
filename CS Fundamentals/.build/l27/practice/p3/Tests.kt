import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BoundedQueueTest {
    private val errors = CopyOnWriteArrayList<Throwable>()

    // A daemon thread that records anything it throws, so a failure shows up in the check.
    private fun spawn(body: () -> Unit) = thread(isDaemon = true) {
        try { body() } catch (e: Throwable) { errors += e }
    }

    private fun waitingOn(q: BoundedQueue<*>, c: Condition) = q.lock.withLock { q.lock.getWaitQueueLength(c) }

    // Polls until `n` threads are parked in await() on `c`.
    private fun awaitParked(q: BoundedQueue<*>, c: Condition, n: Int, threads: List<Thread>, what: String) {
        val deadline = System.nanoTime() + 5_000_000_000
        while (waitingOn(q, c) < n) {
            if (threads.any { !it.isAlive }) fail("$what returned instead of waiting. Errors: $errors")
            if (System.nanoTime() > deadline) fail("$what should be waiting on its condition (await), not spinning")
            Thread.sleep(1)
        }
    }

    @Test fun `items come out in the order they went in`() {
        val q = BoundedQueue<Int>(3)
        q.put(1); q.put(2); q.put(3)
        assertEquals(listOf(1, 2, 3), listOf(q.take(), q.take(), q.take()))
        assertEquals("size", 0, q.size)
    }

    @Test fun `take waits until something is put`() {
        val q = BoundedQueue<String>(2)
        var got: String? = null
        val consumer = spawn { got = q.take() }
        awaitParked(q, q.notEmpty, 1, listOf(consumer), "take() on an empty queue")
        q.put("job")
        consumer.join(2000)
        assertEquals("job", got)
    }

    @Test fun `put waits while the queue is full`() {
        val q = BoundedQueue<String>(2)
        q.put("a"); q.put("b")
        val producer = spawn { q.put("c") }
        awaitParked(q, q.notFull, 1, listOf(producer), "put() on a full queue")
        assertEquals("a full queue holds capacity items", 2, q.size)
        assertEquals("a", q.take())
        producer.join(2000)
        assertEquals(listOf("b", "c"), listOf(q.take(), q.take()))
    }

    @Test fun `a woken taker checks again before taking`() {
        val q = BoundedQueue<String>(2)
        val got = CopyOnWriteArrayList<String>()
        val consumers = List(2) { spawn { got += q.take() } }
        awaitParked(q, q.notEmpty, 2, consumers, "take() on an empty queue")
        q.put("x")                                    // wakes both takers: only one can have it
        val deadline = System.nanoTime() + 5_000_000_000
        while (got.size + errors.size < 2 && !(got.size == 1 && waitingOn(q, q.notEmpty) == 1)) {
            assertTrue("the other taker should go back to waiting", System.nanoTime() < deadline)
            Thread.sleep(1)
        }
        q.put("y")
        consumers.forEach { it.join(2000) }
        assertTrue("a taker found the queue empty after waking: $errors", errors.isEmpty())
        assertEquals(setOf("x", "y"), got.toSet())
    }

    @Test fun `two producers and two consumers move 1,000 items exactly once`() {
        val q = BoundedQueue<Int>(4)
        val sum = AtomicLong()
        val seen = ConcurrentHashMap.newKeySet<Int>()
        val producers = listOf(1..500, 501..1000).map { range -> spawn { range.forEach { q.put(it) } } }
        val consumers = List(2) { spawn { repeat(500) { val n = q.take(); sum.addAndGet(n.toLong()); seen += n } } }
        (producers + consumers).forEach { it.join(5000) }
        assertFalse("a thread is still stuck", (producers + consumers).any { it.isAlive })
        assertTrue("errors: $errors", errors.isEmpty())
        assertEquals("distinct items taken", 1000, seen.size)
        assertEquals("sum of all items", 500_500L, sum.get())
    }
}
