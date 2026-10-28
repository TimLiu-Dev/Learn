// Excerpt of L43 Resilience.kt, lines 10–30 (tested there; not a standalone file)
/** Up to [capacity] tokens, refilled at [perSecond]; each request takes one. */
class TokenBucket(private val capacity: Double, private val perSecond: Double, private val clock: InstantSource) {
    private var tokens = capacity
    private var last = clock.millis()

    @Synchronized fun tryAcquire(): Boolean {
        refill()
        if (tokens < 1) return false
        tokens -= 1
        return true
    }

    /** Whole seconds until a token is free: the value for a Retry-After header. */
    @Synchronized fun retryAfterSeconds(): Long { refill(); return ceil((1 - tokens) / perSecond).toLong().coerceAtLeast(1) }

    private fun refill() {
        val now = clock.millis()
        tokens = min(capacity, tokens + (now - last) / 1000.0 * perSecond)
        last = now
    }
}
