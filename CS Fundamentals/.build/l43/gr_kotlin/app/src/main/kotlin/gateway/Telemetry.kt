package gateway

import java.security.SecureRandom
import java.util.HexFormat
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray

/** One JSON object per line: easy to grep, easy for a log pipeline to parse. */
fun json(vararg fields: Pair<String, Any?>): String = fields.joinToString(",", "{", "}") { (k, v) ->
    "\"$k\":" + when (v) { null -> "null"; is Number, is Boolean -> v.toString(); else -> "\"$v\"" }
}

class Counter { private val n = AtomicLong(); fun inc() = n.incrementAndGet(); fun value() = n.get() }

class Histogram(private val bounds: LongArray) {                       // upper bounds in ms; last slot = +Inf
    private val counts = AtomicLongArray(bounds.size + 1)
    private val sum = AtomicLong()
    fun observe(ms: Long) {
        val i = bounds.indexOfFirst { ms <= it }.let { if (it < 0) bounds.size else it }
        counts.incrementAndGet(i); sum.addAndGet(ms)
    }
    fun count() = (0..bounds.size).sumOf { counts.get(it) }
    fun percentileBound(p: Double): String {
        val target = p * count(); var seen = 0L
        for (i in 0..bounds.size) { seen += counts.get(i); if (seen >= target) return if (i < bounds.size) "${bounds[i]}" else "+Inf" }
        return "+Inf"
    }
    fun render(name: String): String {
        val sb = StringBuilder("# TYPE $name histogram\n")
        var cumulative = 0L
        for (i in 0..bounds.size) {
            cumulative += counts.get(i)
            sb.append("${name}_bucket{le=\"${if (i < bounds.size) bounds[i] else "+Inf"}\"} $cumulative\n")
        }
        sb.append("${name}_sum ${sum.get()}\n${name}_count ${count()}")
        return sb.toString()
    }
}

// ---- W3C Trace Context: traceparent = 00-<32 hex trace-id>-<16 hex parent-id>-<2 hex flags>
private val random = SecureRandom()
fun randomHex(bytes: Int): String = HexFormat.of().formatHex(ByteArray(bytes).also { random.nextBytes(it) })
fun traceparent(traceId: String, spanId: String) = "00-$traceId-$spanId-01"

private val TRACEPARENT = Regex("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$")
/** (traceId, parentId), or null if the header is missing or invalid (all-zero ids are invalid). */
fun parseTraceparent(header: String?): Pair<String, String>? {
    val m = header?.let { TRACEPARENT.matchEntire(it) } ?: return null
    val (traceId, parentId) = m.destructured
    if (traceId.all { it == '0' } || parentId.all { it == '0' }) return null
    return traceId to parentId
}

data class Span(val service: String, val name: String, val traceId: String, val spanId: String,
                val parentId: String?, val startMs: Long, val durMs: Long, val outcome: String)

/** Stands in for a trace collector such as an OpenTelemetry Collector. */
object Tracer {
    private val t0 = System.nanoTime()
    val spans = ConcurrentLinkedQueue<Span>()
    fun nowMs() = (System.nanoTime() - t0) / 1_000_000
    fun record(s: Span) { spans += s }
}
