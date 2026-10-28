package shelf.http

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import java.time.Duration
import java.time.InstantSource
import kotlin.math.ceil
import kotlin.math.min
import shelf.cache.LruCache

// L43's token bucket, unchanged.
class TokenBucket(private val capacity: Double, private val perSecond: Double, private val clock: InstantSource) {
    private var tokens = capacity
    private var last = clock.millis()

    @Synchronized fun tryAcquire(): Boolean {
        refill()
        if (tokens < 1) return false
        tokens -= 1
        return true
    }

    @Synchronized fun retryAfterSeconds(): Long { refill(); return ceil((1 - tokens) / perSecond).toLong().coerceAtLeast(1) }

    private fun refill() {
        val now = clock.millis()
        tokens = min(capacity, tokens + (now - last) / 1000.0 * perSecond)
        last = now
    }
}

/**
 * One bucket per client, checked before the router runs. A com.sun.net.httpserver.Filter
 * wraps every request on its context: call chain.doFilter to continue, or answer and stop.
 */
class RateLimitFilter(
    private val capacity: Double,
    private val perSecond: Double,
    private val clock: InstantSource,
    private val exempt: Set<String> = setOf("/health", "/metrics"),
) : Filter() {
    // Bounded, so a flood of made-up client ids can't grow memory forever (the L24 leak).
    private val buckets = LruCache<String, TokenBucket>(10_000, Duration.ofMinutes(10), java.time.Clock.systemUTC())

    override fun description() = "per-client token bucket"

    override fun doFilter(exchange: HttpExchange, chain: Chain) {
        if (exchange.requestURI.path in exempt) return chain.doFilter(exchange)
        val bucket = buckets.getOrPut(clientOf(exchange)) { TokenBucket(capacity, perSecond, clock) }
        if (bucket.tryAcquire()) return chain.doFilter(exchange)
        val wait = bucket.retryAfterSeconds()
        exchange.use {
            it.send(problem(ApiError(429, "Too Many Requests", "Rate limit exceeded; retry in $wait s"),
                            mapOf("Retry-After" to "$wait")))         // RFC 6585 §4
        }
    }

    companion object {
        /** Who is asking: an API client id if sent, otherwise the caller's IP address. */
        fun clientOf(exchange: HttpExchange): String =
            exchange.requestHeaders.getFirst("X-Client-Id") ?: exchange.remoteAddress.address.hostAddress
    }
}
