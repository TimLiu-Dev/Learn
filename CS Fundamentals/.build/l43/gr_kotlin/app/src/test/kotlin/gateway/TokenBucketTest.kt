package gateway

import java.time.Instant
import java.time.InstantSource
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TokenBucketTest {
    private val now = AtomicLong(0)
    private val clock = InstantSource { Instant.ofEpochMilli(now.get()) }

    @Test fun `a burst up to capacity passes, the next is rejected`() {
        val bucket = TokenBucket(10.0, 10.0, clock)
        repeat(10) { assertTrue(bucket.tryAcquire()) }
        assertFalse(bucket.tryAcquire())
    }

    @Test fun `tokens come back as time passes`() {
        val bucket = TokenBucket(10.0, 10.0, clock)
        repeat(10) { bucket.tryAcquire() }
        now.addAndGet(250)                                                     // 2.5 tokens
        assertEquals(2, (1..5).count { bucket.tryAcquire() })
    }

    @Test fun `retry after is at least one whole second`() {
        val bucket = TokenBucket(1.0, 0.5, clock)
        bucket.tryAcquire()
        assertEquals(2, bucket.retryAfterSeconds())                            // one token every 2 s
    }

    @Test fun `eight threads never get more than capacity`() {
        val bucket = TokenBucket(100.0, 10.0, clock)                           // the clock never moves: no refill
        val granted = AtomicInteger()
        val go = CountDownLatch(1)
        Executors.newFixedThreadPool(8).use { pool ->
            repeat(8) { pool.submit { go.await(); repeat(50) { if (bucket.tryAcquire()) granted.incrementAndGet() } } }
            go.countDown()
        }
        assertEquals(100, granted.get())                                       // 400 tries, exactly 100 tokens
    }
}
