import java.time.Instant
import java.time.InstantSource
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.ceil
import kotlin.math.min

// Token bucket: holds up to `capacity` tokens and refills at `perSecond`.
// Each request takes one token. Bursts up to `capacity` pass at once.
class TokenBucket(private val capacity: Double, private val perSecond: Double, private val clock: InstantSource) {
    private var tokens = capacity
    private var last = clock.millis()

    @Synchronized fun tryAcquire(): Boolean {
        refill()
        if (tokens < 1) return false
        tokens -= 1
        return true
    }

    @Synchronized fun retryAfterSeconds(): Long { refill(); return if (tokens >= 1) 0 else ceil((1 - tokens) / perSecond).toLong() }
    @Synchronized fun tokens(): Double { refill(); return tokens }

    private fun refill() {
        val now = clock.millis()
        tokens = min(capacity, tokens + (now - last) / 1000.0 * perSecond)
        last = now
    }
}

// Sliding window log: remember when each accepted request happened;
// allow a new one only if fewer than `limit` happened in the last `windowMs`.
class SlidingWindowLog(private val limit: Int, private val windowMs: Long, private val clock: InstantSource) {
    private val log = ArrayDeque<Long>()

    @Synchronized fun tryAcquire(): Boolean {
        val now = clock.millis()
        while (log.isNotEmpty() && log.first() <= now - windowMs) log.removeFirst()
        if (log.size >= limit) return false
        log.addLast(now)
        return true
    }
}

fun main() {
    val now = AtomicLong(0)
    val clock = InstantSource { Instant.ofEpochMilli(now.get()) }
    val bucket = TokenBucket(capacity = 5.0, perSecond = 5.0, clock = clock)
    val window = SlidingWindowLog(limit = 5, windowMs = 1000, clock = clock)
    val schedule = listOf(0L to 7, 500L to 3, 1000L to 4, 1200L to 3, 2000L to 6)   // (time, requests)

    println("time   asked  token bucket          sliding window log")
    for ((t, n) in schedule) {
        now.set(t)
        val before = bucket.tokens()
        val b = (1..n).count { bucket.tryAcquire() }
        val w = (1..n).count { window.tryAcquire() }
        println("%4d ms  %2d    %d ok, %d rejected (%.1f→%.1f tokens)   %d ok, %d rejected"
            .format(t, n, b, n - b, before, bucket.tokens(), w, n - w))
    }
}
