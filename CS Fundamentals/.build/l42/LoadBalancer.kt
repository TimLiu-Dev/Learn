import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class Target(val name: String, val uri: URI) {
    @Volatile var healthy = true
    val active = AtomicInteger()    // requests in flight right now
    val served = AtomicInteger()    // requests answered so far
}

enum class Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

// Headers that describe one connection, not the request: each hop sets its own.
val HOP_HEADERS = setOf("connection", "content-length", "host", "transfer-encoding", "upgrade", "expect", "date")

class LoadBalancer(
    private val targets: List<Target>,
    private val strategy: Strategy,
    private val retryOnFailure: Boolean = false,
) : AutoCloseable {
    private val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofMillis(300)).build()
    private val next = AtomicInteger()
    private val started = System.nanoTime()
    @Volatile private var running = true
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 200)
    val uri: URI get() = URI("http://127.0.0.1:${server.address.port}")

    init {
        server.executor = Executors.newVirtualThreadPerTaskExecutor()
        server.createContext("/") { forward(it) }
        server.start()
        Thread.ofVirtual().start { healthLoop() }
    }

    private fun pick(skip: Target?): Target? {
        val up = targets.filter { it.healthy && it !== skip }
        if (up.isEmpty()) return null
        return when (strategy) {
            Strategy.ROUND_ROBIN -> up[Math.floorMod(next.getAndIncrement(), up.size)]
            Strategy.LEAST_CONNECTIONS -> up.minBy { it.active.get() }
        }
    }

    private fun forward(ex: HttpExchange) {
        val body = ex.requestBody.readAllBytes()            // read once, so a retry can resend it
        var t = pick(skip = null)
        while (t != null) {
            t.active.incrementAndGet()
            try {
                val req = HttpRequest.newBuilder(t.uri.resolve(ex.requestURI)).timeout(Duration.ofSeconds(5))
                    .method(ex.requestMethod, HttpRequest.BodyPublishers.ofByteArray(body))
                ex.requestHeaders.forEach { (k, vs) -> if (k.lowercase() !in HOP_HEADERS) vs.forEach { req.header(k, it) } }
                val resp = client.send(req.build(), HttpResponse.BodyHandlers.ofString())
                resp.headers().map().forEach { (k, vs) -> if (k.lowercase() !in HOP_HEADERS) ex.responseHeaders[k] = vs }
                t.served.incrementAndGet()
                return reply(ex, resp.statusCode(), resp.body())
            } catch (e: IOException) {
                // Only GET is safe to send twice (L32): a retried POST could create the book twice.
                if (!retryOnFailure || ex.requestMethod != "GET") return reply(ex, 502, "bad gateway: ${t.name}")
                t.healthy = false                       // passive check: stop using it right away
                log("passive: ${t.name} failed a request, marked DOWN, retrying elsewhere")
            } finally {
                t.active.decrementAndGet()
            }
            t = pick(skip = t)                          // only reached after a failure, with retry on
        }
        reply(ex, 503, "no healthy backend")
    }

    // Active health check: ask every backend for /health every 500 ms.
    private fun healthLoop() {
        while (running) {
            for (t in targets) {
                val ok = try {
                    val req = HttpRequest.newBuilder(t.uri.resolve("/health")).timeout(Duration.ofMillis(300)).build()
                    client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200
                } catch (e: IOException) { false }
                if (ok != t.healthy) {
                    t.healthy = ok
                    log("health check: ${t.name} is ${if (ok) "UP" else "DOWN"}")
                }
            }
            Thread.sleep(500)
        }
    }

    fun log(msg: String) = println("  [%4d ms] %s".format((System.nanoTime() - started) / 1_000_000, msg))

    override fun close() { running = false; server.stop(0) }
}
