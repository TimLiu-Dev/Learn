import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.math.ceil
import kotlin.math.min

class TokenBucket(private val capacity: Double, private val perSecond: Double, private val clock: Clock) {
    private var tokens = capacity
    private var last = clock.millis()
    @Synchronized fun tryAcquire(): Boolean { refill(); if (tokens < 1) return false; tokens -= 1; return true }
    @Synchronized fun retryAfterSeconds(): Long { refill(); return ceil((1 - tokens) / perSecond).toLong().coerceAtLeast(1) }
    private fun refill() { val now = clock.millis(); tokens = min(capacity, tokens + (now - last) / 1000.0 * perSecond); last = now }
}

fun main() {
    val buckets = ConcurrentHashMap<String, TokenBucket>()                     // one bucket per client (L27)
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.executor = Executors.newVirtualThreadPerTaskExecutor()
    server.createContext("/books") { ex ->
        val who = ex.requestHeaders.getFirst("X-Client") ?: ex.remoteAddress.address.hostAddress
        val bucket = buckets.computeIfAbsent(who) { TokenBucket(5.0, 5.0, Clock.systemUTC()) }
        if (bucket.tryAcquire()) {
            ex.sendResponseHeaders(200, -1)
        } else {
            ex.responseHeaders.add("Retry-After", bucket.retryAfterSeconds().toString())
            ex.sendResponseHeaders(429, -1)                                   // 429 Too Many Requests (RFC 6585)
        }
        ex.close()
    }
    server.start()
    val url = URI.create("http://127.0.0.1:${server.address.port}/books")
    val client = HttpClient.newHttpClient()

    val ok = mapOf("alice" to AtomicIntegerArray(3), "bob" to AtomicIntegerArray(3))
    val limited = mapOf("alice" to AtomicIntegerArray(3), "bob" to AtomicIntegerArray(3))
    val start = System.nanoTime()
    fun hammer(who: String, pauseMs: Long) = Thread.ofVirtual().start {
        while (true) {
            val second = ((System.nanoTime() - start) / 1_000_000_000).toInt()
            if (second >= 3) break
            val r = client.send(HttpRequest.newBuilder(url).header("X-Client", who).build(), HttpResponse.BodyHandlers.discarding())
            (if (r.statusCode() == 200) ok else limited)[who]!!.incrementAndGet(second)
            if (who == "alice" && r.statusCode() == 429 && second == 0 && limited[who]!!.get(0) == 1)
                println("first 429 for alice: Retry-After: ${r.headers().firstValue("Retry-After").orElse("?")}")
            Thread.sleep(pauseMs)
        }
    }
    listOf(hammer("alice", 20), hammer("bob", 400)).forEach { it.join() }      // alice ~50/s, bob ~2.5/s
    println("second  alice 200/429   bob 200/429")
    for (s in 0..2) println("%5d   %5d / %-5d   %3d / %d".format(s, ok["alice"]!!.get(s), limited["alice"]!!.get(s), ok["bob"]!!.get(s), limited["bob"]!!.get(s)))
    server.stop(0)
}
