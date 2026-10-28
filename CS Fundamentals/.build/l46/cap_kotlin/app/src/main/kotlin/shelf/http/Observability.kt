package shelf.http

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import java.security.SecureRandom
import java.time.Clock
import java.util.HexFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.atomic.LongAdder

/** One JSON object per line (L43): easy to grep, easy for a log pipeline to parse. */
fun jsonLine(vararg fields: Pair<String, Any?>): String = fields.joinToString(",", "{", "}") { (k, v) ->
    "\"$k\":" + when (v) { null -> "null"; is Number, is Boolean -> v.toString(); else -> "\"" + v.toString().replace("\"", "\\\"") + "\"" }
}

/** The four golden signals (L43): traffic and errors (by status), latency (histogram), saturation (in flight). */
class Metrics {
    private val byStatus = ConcurrentHashMap<Int, LongAdder>()
    private val bounds = longArrayOf(1, 2, 5, 10, 25, 50, 100, 250, 500, 1000)      // ms; one more slot = +Inf
    private val buckets = AtomicLongArray(bounds.size + 1)
    private val sumMs = AtomicLong()
    val inFlight = AtomicInteger()

    fun record(status: Int, ms: Long) {
        byStatus.computeIfAbsent(status) { LongAdder() }.increment()
        val i = bounds.indexOfFirst { ms <= it }.let { if (it < 0) bounds.size else it }
        buckets.incrementAndGet(i)
        sumMs.addAndGet(ms)
    }

    /** Plain text in the shape of Prometheus' exposition format; extra gauges come from the caller. */
    fun render(gauges: Map<String, Number>): String = buildString {
        appendLine("# TYPE shelf_requests_total counter")
        byStatus.toSortedMap().forEach { (code, n) -> appendLine("shelf_requests_total{code=\"$code\"} ${n.sum()}") }
        appendLine("# TYPE shelf_request_ms histogram")
        var cumulative = 0L
        for (i in 0..bounds.size) {
            cumulative += buckets.get(i)
            appendLine("shelf_request_ms_bucket{le=\"${if (i < bounds.size) bounds[i] else "+Inf"}\"} $cumulative")
        }
        appendLine("shelf_request_ms_sum ${sumMs.get()}")
        appendLine("shelf_request_ms_count $cumulative")
        appendLine("shelf_in_flight ${inFlight.get()}")
        gauges.forEach { (name, value) -> appendLine("$name $value") }
    }
}

/**
 * The outermost filter: gives every request an id and a W3C trace context (L43), times it,
 * records metrics, and writes one JSON access-log line, even for requests the rate limiter refused.
 */
class AccessLogFilter(private val metrics: Metrics, private val clock: Clock, private val log: (String) -> Unit) : Filter() {
    private val random = SecureRandom()
    private fun hex(bytes: Int) = HexFormat.of().formatHex(ByteArray(bytes).also(random::nextBytes))

    override fun description() = "request id, trace context, metrics, JSON access log"

    override fun doFilter(exchange: HttpExchange, chain: Chain) {
        val start = System.nanoTime()
        val requestId = exchange.requestHeaders.getFirst("X-Request-Id") ?: hex(8)
        val traceId = parseTraceId(exchange.requestHeaders.getFirst("traceparent")) ?: hex(16)
        val spanId = hex(8)
        exchange.responseHeaders.set("X-Request-Id", requestId)
        exchange.responseHeaders.set("traceparent", "00-$traceId-$spanId-01")
        metrics.inFlight.incrementAndGet()
        try {
            chain.doFilter(exchange)
        } finally {
            metrics.inFlight.decrementAndGet()
            val ms = (System.nanoTime() - start) / 1_000_000
            val status = exchange.responseCode                          // -1 if nothing was sent
            metrics.record(status, ms)
            log(jsonLine(
                "ts" to clock.instant(), "level" to if (status >= 500) "ERROR" else "INFO",
                "requestId" to requestId, "traceId" to traceId, "method" to exchange.requestMethod,
                "path" to exchange.requestURI.path, "status" to status, "ms" to ms,
                "client" to RateLimitFilter.clientOf(exchange),
            ))
        }
    }

    companion object {
        private val TRACEPARENT = Regex("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$")
        /** The caller's trace id, so our log lines join their trace; null if absent or invalid. */
        fun parseTraceId(header: String?): String? =
            header?.let { TRACEPARENT.matchEntire(it) }?.groupValues?.get(1)?.takeUnless { id -> id.all { it == '0' } }
    }
}
