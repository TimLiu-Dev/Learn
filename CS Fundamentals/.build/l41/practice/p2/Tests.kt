import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

// A loader that counts its calls and blocks until the test releases it.
class SlowDb(private val failFirst: Boolean = false) {
    val calls = AtomicInteger()
    val release = CountDownLatch(1)
    fun load(id: Int): String {
        val n = calls.incrementAndGet()
        release.await()
        if (failFirst && n == 1) throw IllegalStateException("db down")
        return "book $id"
    }
}

// Starts n readers of one key, waits until each is parked (inside the loader, or waiting
// for someone else's load), then releases the loader and returns every reader's answer.
fun readAtOnce(n: Int, cache: CoalescingCache<Int, String>, id: Int, db: SlowDb): List<String> {
    val results = Collections.synchronizedList(mutableListOf<String>())
    val threads = List(n) {
        Thread { results += try { cache.get(id) } catch (e: Exception) { "failed" } }
            .apply { isDaemon = true; start() }
    }
    try {
        val deadline = System.nanoTime() + 1_000_000_000
        while (threads.any { it.state != Thread.State.WAITING && it.state != Thread.State.TERMINATED }) {
            check(System.nanoTime() < deadline) {
                "after 1 s, some readers were still not waiting on a load: ${threads.map { it.state }}"
            }
            Thread.yield()
        }
    } finally {
        db.release.countDown()
        threads.forEach { it.join() }
    }
    return results
}

class CoalescingTest {
    init { CoalescingCache<Int, String> { "warm-up" }.get(0) }   // load classes before the threads race

    @Test fun `a second read is served from the cache`() {
        val db = SlowDb().apply { release.countDown() }
        val cache = CoalescingCache(db::load)
        assertEquals("book 1", cache.get(1))
        assertEquals("book 1", cache.get(1))
        assertEquals("loader calls", 1, db.calls.get())
    }

    @Test fun `8 concurrent misses for one key cause one load`() {
        val db = SlowDb()
        val results = readAtOnce(8, CoalescingCache(db::load), 7, db)
        assertEquals("every reader's answer", List(8) { "book 7" }, results)
        assertEquals("loader calls for 8 concurrent misses", 1, db.calls.get())
    }

    @Test fun `a failed load reaches every waiter, then the key can be retried`() {
        val db = SlowDb(failFirst = true)
        val cache = CoalescingCache(db::load)
        val results = readAtOnce(4, cache, 7, db)
        assertEquals("every reader's answer", List(4) { "failed" }, results)
        assertEquals("loader calls so far", 1, db.calls.get())
        assertEquals("a retry after the failure", "book 7", cache.get(7))
        assertEquals("loader calls after the retry", 2, db.calls.get())
    }

    @Test fun `a failed load is not cached`() {
        val db = SlowDb(failFirst = true).apply { release.countDown() }
        val cache = CoalescingCache(db::load)
        assertThrows(IllegalStateException::class.java) { cache.get(3) }
        assertEquals("book 3", cache.get(3))
    }

    @Test fun `a slow load does not block other keys`() {
        val db = SlowDb()
        val cache = CoalescingCache<Int, String> { id -> if (id == 1) db.load(id) else "book $id" }
        val slow = Thread { cache.get(1) }.apply { isDaemon = true; start() }
        try {
            while (db.calls.get() == 0) Thread.yield()               // key 1's load has started
            val other = Thread { cache.get(2) }.apply { isDaemon = true; start() }
            other.join(2000)
            assertFalse("get(2) was still waiting for key 1's load after 2 s", other.isAlive)
        } finally {
            db.release.countDown()
            slow.join()
        }
    }
}
