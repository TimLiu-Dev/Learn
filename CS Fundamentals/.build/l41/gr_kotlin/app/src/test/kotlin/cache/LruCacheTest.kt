package cache

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Random
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** A clock the test moves by hand (L38: inject time). */
class FakeClock(private var now: Instant = Instant.parse("2026-10-26T12:00:00Z")) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId?): Clock = this
    override fun instant(): Instant = now
    fun advance(d: Duration) { now = now.plus(d) }
}

class LruCacheTest {
    private val clock = FakeClock()
    private fun cache(cap: Int = 3) = LruCache<String, Int>(cap, Duration.ofSeconds(10), clock)

    @Test fun `evicts the least recently used entry when full`() {
        val c = cache()
        c.put("a", 1); c.put("b", 2); c.put("c", 3)
        c.put("d", 4)                                   // "a" is the oldest
        assertNull(c.get("a"))
        assertEquals(listOf("d", "c", "b"), c.keys())
    }

    @Test fun `get makes an entry recently used`() {
        val c = cache()
        c.put("a", 1); c.put("b", 2); c.put("c", 3)
        c.get("a")                                      // now "b" is the oldest
        c.put("d", 4)
        assertNull(c.get("b"))
        assertEquals(1, c.get("a"))
    }

    @Test fun `put on an existing key updates without evicting`() {
        val c = cache()
        c.put("a", 1); c.put("b", 2); c.put("c", 3)
        c.put("a", 10)
        assertEquals(3, c.size)
        assertEquals(10, c.get("a"))
        assertEquals(0, c.stats().evictions)
    }

    @Test fun `entries expire after the ttl`() {
        val c = cache()
        c.put("a", 1)
        clock.advance(Duration.ofSeconds(9))
        assertEquals(1, c.get("a"))
        clock.advance(Duration.ofSeconds(1))            // exactly 10 s: expired
        assertNull(c.get("a"))
        assertEquals(0, c.size)                         // an expired entry is dropped on read
    }

    @Test fun `stats count hits misses and evictions`() {
        val c = cache(cap = 1)
        c.put("a", 1); c.get("a"); c.get("x"); c.put("b", 2)
        assertEquals(Stats(hits = 1, misses = 1, evictions = 1), c.stats())
    }

    @Test fun `stays within capacity under 8 threads`() {
        val c = LruCache<Int, Int>(100, Duration.ofHours(1))
        val pool = Executors.newFixedThreadPool(8)
        val gate = CountDownLatch(1)
        val errors = java.util.concurrent.ConcurrentLinkedQueue<Throwable>()
        repeat(8) { t ->
            pool.submit {
                gate.await()
                val rnd = Random(41L + t)
                try {
                    repeat(100_000) {
                        val k = rnd.nextInt(1_000)
                        if (rnd.nextBoolean()) c.put(k, k) else c.get(k)?.let { v -> check(v == k) }
                    }
                } catch (e: Throwable) { errors += e }
            }
        }
        gate.countDown()
        pool.shutdown()
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS))
        assertTrue(errors.isEmpty(), "errors: $errors")
        assertEquals(100, c.size)
        assertEquals(100, c.keys().toSet().size)        // list and map agree: no lost or duplicate nodes
    }
}
