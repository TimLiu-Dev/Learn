package shelf

import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import shelf.store.BookStore

/** A clock the test moves by hand (L38), so rate-limit refills need no sleeping. */
class MutableClock(var now: Instant = Instant.parse("2026-10-27T10:00:00Z")) : Clock() {
    override fun instant(): Instant = now
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId) = this
}

/** Counts reads that reach the real store, and can make them slow. */
class SpyBookStore(private val inner: BookStore, private val delayMs: Long = 0) : BookStore by inner {
    val finds = AtomicInteger()
    override fun find(id: Long) = finds.incrementAndGet().let { Thread.sleep(delayMs); inner.find(id) }
}

// Integration tests for the v2 additions: each starts a whole Shelf on port 0 with a temp database.
class ProductionTest {
    private val dbFile = Files.createTempFile("shelf-prod", ".db")
    private val clock = MutableClock()
    private val logs = CopyOnWriteArrayList<String>()
    private var spy: SpyBookStore? = null
    private var shelf: Shelf? = null
    private val client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build()

    private fun start(config: Config = Config(port = 0, dbFile = dbFile, drainSeconds = 0), delayMs: Long = 0) =
        Shelf(config.copy(port = 0, dbFile = dbFile), clock, logs::add) { SpyBookStore(it, delayMs).also { s -> spy = s } }
            .start().also { shelf = it }

    @AfterTest
    fun tearDown() {
        shelf?.close()
        Files.deleteIfExists(dbFile)
    }

    private fun call(method: String, path: String, body: String? = null, vararg headers: String): HttpResponse<String> {
        val b = HttpRequest.newBuilder(URI("http://127.0.0.1:${shelf!!.port}$path"))
            .method(method, body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/json")
        headers.toList().chunked(2).forEach { (k, v) -> b.header(k, v) }
        return client.send(b.build(), HttpResponse.BodyHandlers.ofString())
    }

    @Test
    fun `20 simultaneous loans of one book give one 201 and nineteen 409s, never a 500`() {
        start()
        call("POST", "/books", """{"title":"Dune","author":"Frank Herbert"}""")
        repeat(20) { call("POST", "/members", """{"name":"M$it","email":"m$it@example.com"}""") }
        val gate = CountDownLatch(1)
        val codes = Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            (1..20).map { m -> pool.submit<Int> { gate.await(); call("POST", "/loans", """{"bookId":1,"memberId":$m}""").statusCode() } }
                .also { gate.countDown() }.map { it.get() }
        }
        assertEquals(mapOf(201 to 1, 409 to 19), codes.groupingBy { it }.eachCount())
    }

    @Test
    fun `the cache serves repeat reads and an update invalidates it`() {
        start()
        call("POST", "/books", """{"title":"Dnue","author":"Frank Herbert"}""")
        repeat(5) { call("GET", "/books/1") }
        assertEquals(1, spy!!.finds.get())                       // 5 reads, 1 trip to SQLite
        call("PUT", "/books/1", """{"title":"Dune","author":"Frank Herbert"}""")
        assertTrue("\"Dune\"" in call("GET", "/books/1").body())  // not the stale "Dnue"
        assertEquals(2, spy!!.finds.get())
    }

    @Test
    fun `a client over its limit gets 429 with Retry-After until tokens refill`() {
        start(Config(rateCapacity = 3.0, ratePerSecond = 1.0, drainSeconds = 0))
        repeat(3) { assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "ada").statusCode()) }
        val refused = call("GET", "/books/9", null, "X-Client-Id", "ada")
        assertEquals(429, refused.statusCode())
        assertEquals("1", refused.headers().firstValue("Retry-After").get())
        assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "bob").statusCode())   // other clients unaffected
        assertEquals(200, call("GET", "/health", null, "X-Client-Id", "ada").statusCode())    // exempt
        clock.now = clock.now.plusSeconds(1)                     // one token back
        assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "ada").statusCode())
    }

    @Test
    fun `every request gets one JSON log line that joins the caller's trace, and metrics count it`() {
        start()
        val trace = "4bf92f3577b34da6a3ce929d0e0e4736"
        val r = call("GET", "/books/7", null, "traceparent", "00-$trace-00f067aa0ba902b7-01", "X-Request-Id", "req-1")
        assertEquals("req-1", r.headers().firstValue("X-Request-Id").get())
        assertTrue(r.headers().firstValue("traceparent").get().startsWith("00-$trace-"))
        val line = logs.single { "\"req-1\"" in it }
        assertTrue(""""traceId":"$trace"""" in line && """"status":404""" in line && """"path":"/books/7"""" in line, line)
        assertTrue("""shelf_requests_total{code="404"} 1""" in call("GET", "/metrics").body())
    }

    @Test
    fun `shutdown lets an in-flight request finish, then refuses new ones`() {
        start(Config(drainSeconds = 5), delayMs = 500)
        call("POST", "/books", """{"title":"Dune","author":"Frank Herbert"}""")
        val slow = client.sendAsync(HttpRequest.newBuilder(URI("http://127.0.0.1:${shelf!!.port}/books/1")).build(),
                                    HttpResponse.BodyHandlers.ofString())
        Thread.sleep(100)                                        // the request is now inside the slow store
        val port = shelf!!.port
        val took = kotlin.system.measureTimeMillis { shelf!!.close() }
        shelf = null
        assertEquals(200, slow.get().statusCode())               // it finished
        assertTrue(took < 2000, "close() took $took ms")         // and close() didn't wait the full 5 s
        assertFailsWith<ConnectException> {
            client.send(HttpRequest.newBuilder(URI("http://127.0.0.1:$port/health")).build(), HttpResponse.BodyHandlers.ofString())
        }
    }
}
