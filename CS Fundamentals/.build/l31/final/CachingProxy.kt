import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

// Client -> caching proxy (port B) -> origin (port A). Everything stops when the script ends.
data class Entry(val body: String, val etag: String?, val cacheControl: String, val freshUntil: Long)

val cache = ConcurrentHashMap<String, Entry>()
val originHits = ConcurrentHashMap<String, Int>()
val origin304s = AtomicInteger()
val http: HttpClient = HttpClient.newHttpClient()

fun reply(ex: HttpExchange, status: Int, body: String, headers: Map<String, String>) {
    headers.forEach { (k, v) -> ex.responseHeaders[k] = v }
    val bytes = body.encodeToByteArray()
    ex.sendResponseHeaders(status, if (status == 304) -1 else bytes.size.toLong())
    ex.responseBody.use { if (status != 304) it.write(bytes) }
}

fun freshUntil(cacheControl: String, now: Long): Long {
    if ("no-cache" in cacheControl) return now                             // store, but always revalidate
    val maxAge = cacheControl.split(",").map { it.trim() }.firstOrNull { it.startsWith("max-age=") }
    return if (maxAge == null) now else now + 1000L * maxAge.removePrefix("max-age=").toInt()
}

fun startOrigin(): HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
    createContext("/") { ex ->
        val path = ex.requestURI.path
        originHits.merge(path, 1, Int::plus)
        when (path) {
            "/time" -> reply(ex, 200, LocalTime.now().truncatedTo(ChronoUnit.MILLIS).toString(),
                mapOf("Cache-Control" to "max-age=2"))
            "/report" -> {
                val etag = "\"r-v1\""
                val h = mapOf("ETag" to etag, "Cache-Control" to "no-cache")
                if (ex.requestHeaders.getFirst("If-None-Match") == etag) {
                    origin304s.incrementAndGet()
                    reply(ex, 304, "", h)
                } else reply(ex, 200, "Q3 report: revenue up 4%", h)
            }
            "/me" -> reply(ex, 200, "hello, Ada", mapOf("Cache-Control" to "private, max-age=60"))
            else -> reply(ex, 404, "no such page", emptyMap())
        }
    }
    start()
}

fun startProxy(originPort: Int): HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
    createContext("/") { ex ->
        val path = ex.requestURI.path
        val now = System.currentTimeMillis()
        val e = cache[path]
        if (e != null && now < e.freshUntil) {                             // fresh: origin not contacted
            reply(ex, 200, e.body, mapOf("X-Cache" to "HIT", "Cache-Control" to e.cacheControl))
            return@createContext
        }
        val req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$originPort$path"))
        e?.etag?.let { req.header("If-None-Match", it) }
        val r = http.send(req.build(), HttpResponse.BodyHandlers.ofString())
        val cc = r.headers().firstValue("Cache-Control").orElse("")
        if (r.statusCode() == 304 && e != null) {                          // still valid: refresh and reuse
            cache[path] = e.copy(cacheControl = cc, freshUntil = freshUntil(cc, now))
            reply(ex, 200, e.body, mapOf("X-Cache" to "REVALIDATED", "Cache-Control" to cc))
            return@createContext
        }
        // a shared cache must not keep no-store or private responses (RFC 9111 §5.2.2.5, §5.2.2.7)
        val storable = r.statusCode() == 200 && "no-store" !in cc && "private" !in cc
        if (storable) cache[path] = Entry(r.body(), r.headers().firstValue("ETag").orElse(null), cc, freshUntil(cc, now))
        reply(ex, r.statusCode(), r.body(),
            mapOf("X-Cache" to if (storable) "MISS" else "MISS (not stored)", "Cache-Control" to cc))
    }
    start()
}

fun main() {
    val origin = startOrigin()
    val proxy = startProxy(origin.address.port)
    val base = "http://127.0.0.1:${proxy.address.port}"
    val script = listOf("/time", "/time", "/time", "sleep", "/time",
                        "/report", "/report", "/report", "/me", "/me", "/nope")
    try {
        for (step in script) {
            if (step == "sleep") { Thread.sleep(2500); println("... 2.5 s later"); continue }
            val r = http.send(HttpRequest.newBuilder(URI.create(base + step)).build(),
                HttpResponse.BodyHandlers.ofString())
            println("GET %-8s %d  %-18s %s".format(step, r.statusCode(),
                r.headers().firstValue("X-Cache").orElse("?"), r.body()))
        }
        println("origin saw ${originHits.toSortedMap()}, of which $origin304s were 304 Not Modified")
    } finally {
        proxy.stop(0)
        origin.stop(0)
    }
}
