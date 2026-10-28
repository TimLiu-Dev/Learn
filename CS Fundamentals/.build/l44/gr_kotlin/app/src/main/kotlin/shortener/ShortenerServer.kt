package shortener

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ShortenerServer(
    private val store: LinkStore,
    private val clock: Clock = Clock.systemUTC(),
    cacheCapacity: Int = 10_000,
    private val limiter: RateLimiter = RateLimiter(capacity = 10.0, perSecond = 1.0),
    port: Int = 0,
    private val verbose: Boolean = false,
) {
    private val json = Json { explicitNulls = false }
    val cache = LruCache<String, Lookup>(cacheCapacity)
    val clicks = ClickRecorder(store, clock)
    private val idempotent = ConcurrentHashMap<String, LinkView>()     // per process: see the design doc
    private val pool = Executors.newVirtualThreadPerTaskExecutor()   // L28
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0)

    init {
        server.createContext("/") { ex -> ex.use { handle(it) } }
        server.executor = pool
    }

    val port: Int get() = server.address.port
    fun start() = server.start()
    fun stop() {
        server.stop(0)
        pool.close()
        clicks.stop()
    }

    private fun handle(ex: HttpExchange) {
        try {
            val parts = ex.requestURI.path.trim('/').split('/')
            when {
                parts == listOf("api", "links") ->
                    if (ex.requestMethod == "POST") create(ex) else notAllowed(ex, "POST")
                parts.size == 4 && parts[0] == "api" && parts[1] == "links" && parts[3] == "stats" ->
                    if (ex.requestMethod == "GET") stats(ex, parts[2]) else notAllowed(ex, "GET")
                parts.size == 1 && parts[0].isNotEmpty() && parts[0] != "api" ->
                    if (ex.requestMethod == "GET") redirect(ex, parts[0]) else notAllowed(ex, "GET")
                else -> problem(ex, 404, "Not Found", "No resource at ${ex.requestURI.path}")
            }
        } catch (e: SerializationException) {
            problem(ex, 400, "Bad Request", "Malformed JSON body")
        } catch (e: Exception) {
            System.err.println("500 on ${ex.requestURI}: $e")   // details to the log, not the client (L33)
            problem(ex, 500, "Internal Server Error", "Something went wrong")
        }
    }

    private fun create(ex: HttpExchange) {
        val client = ex.requestHeaders.getFirst("X-Client-Id") ?: ex.remoteAddress.address.hostAddress
        val wait = limiter.tryAcquire(client)
        if (wait > 0) {
            ex.responseHeaders.set("Retry-After", wait.toString())          // RFC 9110 §10.2.3
            return problem(ex, 429, "Too Many Requests", "Slow down: try again in $wait s")
        }
        val input = json.decodeFromString<NewLink>(ex.requestBody.readAllBytes().decodeToString())
        validate(input)?.let { return problem(ex, 400, "Invalid link", it) }

        val key = ex.requestHeaders.getFirst("Idempotency-Key")
        val view = try {
            if (key == null) newLink(input) else idempotent.computeIfAbsent("$client:$key") { newLink(input) }
        } catch (e: AliasTakenException) {
            return problem(ex, 409, "Conflict", e.message!!)
        }
        ex.responseHeaders.set("Location", "/${view.code}")
        send(ex, 201, json.encodeToString(view))
    }

    private fun validate(input: NewLink): String? {
        val url = input.url ?: return "url is required"
        val uri = runCatching { URI(url) }.getOrNull()
        if (url.length > 2048 || uri?.scheme !in setOf("http", "https") || uri?.host == null)
            return "url must be an absolute http(s) URL of at most 2048 characters"
        if (input.alias != null && !input.alias.matches(Regex("[A-Za-z0-9-]{4,30}")))
            return "alias must be 4-30 letters, digits or '-'"
        if (input.ttlSeconds != null && input.ttlSeconds < 1) return "ttlSeconds must be at least 1"
        return null
    }

    private fun newLink(input: NewLink): LinkView {
        val now = clock.millis()
        val link = store.create(input.url!!, input.alias, now, input.ttlSeconds?.let { now + it * 1000 })
        return LinkView(link.code, link.url, "http://127.0.0.1:$port/${link.code}", link.expiresAt)
    }

    /** The hot path: cache, then database, then remember the answer (cache-aside, L41). */
    private fun lookup(code: String): Lookup =
        cache.get(code) ?: (store.find(code)?.let { Lookup.Found(it) } ?: Lookup.Missing).also { cache.put(code, it) }

    private fun redirect(ex: HttpExchange, code: String) {
        when (val found = lookup(code)) {
            Lookup.Missing -> problem(ex, 404, "Not Found", "No link '$code'")
            is Lookup.Found -> {
                val expiresAt = found.link.expiresAt
                if (expiresAt != null && expiresAt <= clock.millis()) {
                    return problem(ex, 410, "Gone", "Link '$code' has expired")
                }
                clicks.record(code)                                     // async: the redirect doesn't wait
                ex.responseHeaders.set("Location", found.link.url)
                send(ex, 302, null)
            }
        }
    }

    private fun stats(ex: HttpExchange, code: String) {
        val link = (lookup(code) as? Lookup.Found)?.link ?: return problem(ex, 404, "Not Found", "No link '$code'")
        send(ex, 200, json.encodeToString(Stats(code, link.url, store.clicks(code))))
    }

    private fun notAllowed(ex: HttpExchange, allow: String) {
        ex.responseHeaders.set("Allow", allow)
        problem(ex, 405, "Method Not Allowed", "${ex.requestMethod} is not supported here")
    }

    private fun problem(ex: HttpExchange, status: Int, title: String, detail: String) =
        send(ex, status, json.encodeToString(Problem("about:blank", title, status, detail)), "application/problem+json")

    private fun send(ex: HttpExchange, status: Int, body: String?, type: String = "application/json") {
        if (verbose) println("${ex.requestMethod} ${ex.requestURI} -> $status")
        if (body == null) return ex.sendResponseHeaders(status, -1)
        val bytes = body.encodeToByteArray()
        ex.responseHeaders.set("Content-Type", type)
        ex.sendResponseHeaders(status, bytes.size.toLong())
        ex.responseBody.use { it.write(bytes) }
    }
}
