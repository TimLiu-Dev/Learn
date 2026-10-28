package gateway

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.Random
import java.util.concurrent.Executors

/**
 * A deliberately flaky pricing service: 20% of calls fail with 500, 5% hang for 2 s.
 * During [outage] (ms since start) every call fails with 503, to trip the breaker.
 */
class PricingService(seed: Long, private val outage: LongRange) {
    private val random = Random(seed)
    private val start = System.nanoTime()
    val server: HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)

    init {
        server.executor = Executors.newVirtualThreadPerTaskExecutor()
        server.createContext("/price") { ex ->
            val begin = Tracer.nowMs()
            val (traceId, parentId) = parseTraceparent(ex.requestHeaders.getFirst("traceparent")) ?: (randomHex(16) to null)
            val id = ex.requestURI.path.substringAfterLast('/').toIntOrNull() ?: 0
            val roll = synchronized(random) { random.nextInt(100) }
            val elapsed = (System.nanoTime() - start) / 1_000_000
            val status = when {
                elapsed in outage -> 503
                roll < 20 -> 500
                roll < 25 -> { Thread.sleep(2000); 200 }
                else -> 200
            }
            val body = if (status == 200) "%.2f".format(10.0 + id % 7) else "error"
            Tracer.record(Span("pricing", "GET ${ex.requestURI}", traceId, randomHex(8), parentId, begin, Tracer.nowMs() - begin, "$status"))
            val bytes = body.toByteArray()
            ex.sendResponseHeaders(status, bytes.size.toLong())
            ex.responseBody.use { it.write(bytes) }
        }
    }
}
