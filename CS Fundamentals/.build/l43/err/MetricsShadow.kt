import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray

// Hand-written metrics: what Micrometer or a Prometheus client gives you, in miniature.
class Counter(val name: String, val help: String) {
    private val n = AtomicLong()
    fun inc() = n.incrementAndGet()
    fun value() = n.get()
}

class Histogram(val name: String, val help: String, private val bounds: LongArray) {   // upper bounds in ms
    private val counts = AtomicLongArray(bounds.size + 1)                             // last slot = +Inf
    private val sum = AtomicLong()
    fun observe(ms: Long) {
        val i = bounds.indexOfFirst { ms <= it }.let { if (it < 0) bounds.size else it }
        counts.incrementAndGet(i); sum.addAndGet(ms)
    }
    fun count() = (0..bounds.size).sumOf { counts.get(it) }
    // Estimate a percentile: the upper bound of the bucket where the running total crosses it.
    fun percentile(p: Double): String {
        val target = p * count()
        var seen = 0L
        for (i in 0..bounds.size) {
            seen += counts.get(i)
            if (seen >= target) return if (i < bounds.size) "≤ ${bounds[i]} ms" else "> ${bounds.last()} ms"
        }
        return "-"
    }
    fun render() = buildString {
        appendLine("# HELP $name $help")
        appendLine("# TYPE $name histogram")
        var cumulative = 0L
        for (i in 0..bounds.size) {
            cumulative += counts.get(i)
            val le = if (i < bounds.size) bounds[i].toString() else "+Inf"
            appendLine("${name}_bucket{le=\"$le\"} $cumulative")
        }
        appendLine("${name}_sum ${sum.get()}")
        append("${name}_count ${count()}")
    }
}

fun main() {
    val requests = Counter("http_requests_total", "Requests received.")
    val errors = Counter("http_errors_total", "Requests that failed (5xx).")
    val latency = Histogram("http_request_duration_ms", "Request latency.", longArrayOf(5, 10, 25, 50, 100, 250, 500, 1000))
    var inFlight = 0                                              // a gauge: a value that goes up and down

    val random = java.util.Random(43)
    repeat(1000) {
        inFlight++
        requests.inc()
        val ms = if (random.nextInt(100) < 4) 300L + random.nextInt(700) else 8L + random.nextInt(30)   // 4% slow
        if (random.nextInt(100) < 2) errors.inc()
        latency.observe(ms)
        inFlight--
    }

    println("GET /metrics")
    println("# TYPE ${requests.name} counter\n${requests.name} ${requests.value()}")
    println("# TYPE ${errors.name} counter\n${errors.name} ${errors.value()}")
    println("# TYPE http_in_flight gauge\nhttp_in_flight $inFlight")
    println(latency.render())
    println()
    println("golden signals: traffic=${requests.value()} requests, errors=%.1f%%, latency p50 %s, p95 %s, p99 %s, saturation in-flight=$inFlight"
        .format(100.0 * errors.value() / requests.value(), latency.percentile(0.50), latency.percentile(0.95), latency.percentile(0.99)))
}
