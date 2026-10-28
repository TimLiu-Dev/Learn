package gateway

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.InstantSource
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * GET /api/price/{id}: rate limit → retry (GETs only) → circuit breaker → 300 ms timeout → pricing.
 * When pricing can't answer, serve the last good price we saw (marked stale).
 */
class Gateway(private val pricingPort: Int, clock: InstantSource) {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(300)).build()
    private val buckets = ConcurrentHashMap<String, TokenBucket>()
    private val lastGood = ConcurrentHashMap<Int, String>()
    private val jitter = Random(43)
    private val bucketClock = clock
    val breaker = CircuitBreaker(5, Duration.ofSeconds(1), clock) { from, to ->
        println(json("level" to "WARN", "service" to "gateway", "msg" to "circuit $from -> $to", "t_ms" to Tracer.nowMs()))
    }

    // the four golden signals
    val requests = Counter(); val ok = Counter(); val stale = Counter(); val limited = Counter(); val errors = Counter()
    val latency = Histogram(longArrayOf(5, 10, 25, 50, 100, 250, 500, 1000, 2000))
    val inFlight = AtomicInteger()

    val server: HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        executor = Executors.newVirtualThreadPerTaskExecutor()
        createContext("/api/price") { ex -> handle(ex) }
        createContext("/metrics") { ex -> send(ex, 200, metrics()) }
    }

    private fun handle(ex: HttpExchange) {
        if (ex.requestMethod != "GET") { ex.responseHeaders.add("Allow", "GET"); send(ex, 405, "only GET"); return }
        val begin = Tracer.nowMs()
        requests.inc(); inFlight.incrementAndGet()
        try {
            val who = ex.requestHeaders.getFirst("X-Client") ?: "anonymous"
            val bucket = buckets.computeIfAbsent(who) { TokenBucket(10.0, 10.0, bucketClock) }
            if (!bucket.tryAcquire()) {
                limited.inc()
                ex.responseHeaders.add("Retry-After", bucket.retryAfterSeconds().toString())
                send(ex, 429, "slow down"); return
            }
            val id = ex.requestURI.path.substringAfterLast('/').toIntOrNull() ?: 0
            val traceId = randomHex(16); val rootSpan = randomHex(8)
            val result = try {
                val price = retry(3, 50, 400, jitter, retryable = { it is IOException }) { attempt ->
                    breaker.call { fetch(id, attempt, traceId, rootSpan) }      // GET is idempotent: safe to retry
                }
                lastGood[id] = price; ok.inc()
                send(ex, 200, """{"id":$id,"price":$price}"""); "200"
            } catch (e: Exception) {                                          // breaker open or out of retries
                val cached = lastGood[id]
                if (cached != null) {
                    stale.inc(); ex.responseHeaders.add("X-Fallback", "true")
                    send(ex, 200, """{"id":$id,"price":$cached,"stale":true}"""); "200 stale"
                } else {
                    errors.inc(); ex.responseHeaders.add("Retry-After", "1")
                    send(ex, 503, "pricing unavailable"); "503"
                }
            }
            Tracer.record(Span("gateway", "GET ${ex.requestURI}", traceId, rootSpan, null, begin, Tracer.nowMs() - begin, result))
        } finally {
            latency.observe(Tracer.nowMs() - begin); inFlight.decrementAndGet()
        }
    }

    private fun fetch(id: Int, attempt: Int, traceId: String, parent: String): String {
        val span = randomHex(8); val begin = Tracer.nowMs()
        val request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$pricingPort/price/$id"))
            .timeout(Duration.ofMillis(300))                                  // never wait forever
            .header("traceparent", traceparent(traceId, span)).build()
        var outcome = "ok"
        try {
            val r = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (r.statusCode() >= 500) { outcome = "${r.statusCode()}"; throw IOException("pricing ${r.statusCode()}") }
            return r.body()
        } catch (e: java.net.http.HttpTimeoutException) {
            outcome = "timeout"; throw e                                      // HttpTimeoutException is an IOException
        } finally {
            Tracer.record(Span("gateway", "attempt $attempt", traceId, span, parent, begin, Tracer.nowMs() - begin, outcome))
        }
    }

    fun metrics() = buildString {
        appendLine("# TYPE gateway_requests_total counter\ngateway_requests_total ${requests.value()}")
        appendLine("# TYPE gateway_responses_total counter")
        appendLine("gateway_responses_total{result=\"ok\"} ${ok.value()}")
        appendLine("gateway_responses_total{result=\"stale\"} ${stale.value()}")
        appendLine("gateway_responses_total{result=\"limited\"} ${limited.value()}")
        appendLine("gateway_responses_total{result=\"error\"} ${errors.value()}")
        appendLine("# TYPE gateway_in_flight gauge\ngateway_in_flight ${inFlight.get()}")
        appendLine("# TYPE gateway_circuit_open gauge\ngateway_circuit_open ${if (breaker.state() == CircuitBreaker.State.CLOSED) 0 else 1}")
        append(latency.render("gateway_request_duration_ms"))
    }

    private fun send(ex: HttpExchange, status: Int, body: String) {
        val bytes = body.toByteArray()
        ex.sendResponseHeaders(status, bytes.size.toLong())
        ex.responseBody.use { it.write(bytes) }
    }
}
