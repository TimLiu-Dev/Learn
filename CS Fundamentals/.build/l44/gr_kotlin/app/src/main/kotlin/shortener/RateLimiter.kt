package shortener

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
import kotlin.math.min

/** Token bucket per client (L43): `capacity` burst, refilled at `perSecond` tokens/s. */
class RateLimiter(private val capacity: Double, private val perSecond: Double,
                  private val nanoTime: () -> Long = System::nanoTime) {
    private class Bucket(var tokens: Double, var last: Long)
    private val buckets = ConcurrentHashMap<String, Bucket>()

    /** 0 if allowed, otherwise the whole seconds to wait (for Retry-After). */
    fun tryAcquire(client: String): Long {
        val now = nanoTime()
        val b = buckets.computeIfAbsent(client) { Bucket(capacity, now) }
        synchronized(b) {
            b.tokens = min(capacity, b.tokens + (now - b.last) / 1e9 * perSecond)
            b.last = now
            if (b.tokens >= 1) {
                b.tokens -= 1
                return 0
            }
            return ceil((1 - b.tokens) / perSecond).toLong()
        }
    }
}
