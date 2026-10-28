package shortener

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** A clock the test can move forward (L38: inject time, don't read it). */
class TestClock(var now: Instant = Instant.parse("2026-10-26T12:00:00Z")) : Clock() {
    override fun instant() = now
    override fun getZone() = ZoneOffset.UTC
    override fun withZone(zone: java.time.ZoneId?) = this
}

class ShortenerTest {
    private val db = Files.createTempFile("test", ".db")
    private val clock = TestClock()
    private val store = LinkStore(db.toString())
    private val server = ShortenerServer(store, clock, limiter = RateLimiter(capacity = 3.0, perSecond = 0.001))
    private val http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()

    @BeforeTest fun start() = server.start()
    @AfterTest fun stop() { server.stop(); Files.deleteIfExists(db) }

    private fun get(path: String) =
        http.send(HttpRequest.newBuilder(URI("http://127.0.0.1:${server.port}$path")).build(), HttpResponse.BodyHandlers.ofString())

    private fun post(body: String, client: String = "c1", key: String? = null): HttpResponse<String> {
        val b = HttpRequest.newBuilder(URI("http://127.0.0.1:${server.port}/api/links"))
            .header("X-Client-Id", client).POST(HttpRequest.BodyPublishers.ofString(body))
        if (key != null) b.header("Idempotency-Key", key)
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString())
    }

    @Test fun `base62 round-trips`() {
        for (n in listOf(0L, 61L, 62L, 916_132_832L, Long.MAX_VALUE)) assertEquals(n, Base62.decode(Base62.encode(n)))
    }

    @Test fun `create returns 201 and the code redirects with 302`() {
        val res = post("""{"url":"https://example.com/a"}""")
        assertEquals(201, res.statusCode())
        val location = res.headers().firstValue("Location").get()
        assertTrue(Regex("/[0-9a-zA-Z]{7}").matches(location), location)
        val hop = get(location)
        assertEquals(302, hop.statusCode())
        assertEquals("https://example.com/a", hop.headers().firstValue("Location").get())
    }

    @Test fun `custom alias works once, then conflicts`() {
        assertEquals(201, post("""{"url":"https://example.com/docs","alias":"docs"}""").statusCode())
        val again = post("""{"url":"https://example.com/other","alias":"docs"}""", client = "c2")
        assertEquals(409, again.statusCode())
        assertEquals("application/problem+json", again.headers().firstValue("Content-Type").get())
    }

    @Test fun `bad urls are rejected with a problem`() {
        val res = post("""{"url":"javascript:alert(1)"}""")
        assertEquals(400, res.statusCode())
        assertTrue("http(s)" in res.body())
    }

    @Test fun `unknown codes are 404 and the miss is cached`() {
        assertEquals(404, get("/nope123").statusCode())
        val before = store.queries.get()
        assertEquals(404, get("/nope123").statusCode())
        assertEquals(before, store.queries.get(), "second miss must not touch the database")
    }

    @Test fun `expired links are 410 Gone`() {
        val code = post("""{"url":"https://example.com/sale","ttlSeconds":60}""").headers().firstValue("Location").get()
        assertEquals(302, get(code).statusCode())
        clock.now = clock.now.plusSeconds(61)
        assertEquals(410, get(code).statusCode())
    }

    @Test fun `fourth create in a burst is rate limited`() {
        repeat(3) { assertEquals(201, post("""{"url":"https://example.com/$it"}""", client = "spammer").statusCode()) }
        val res = post("""{"url":"https://example.com/4"}""", client = "spammer")
        assertEquals(429, res.statusCode())
        assertTrue(res.headers().firstValue("Retry-After").isPresent)
    }

    @Test fun `same idempotency key returns the same link`() {
        val a = post("""{"url":"https://example.com/once"}""", key = "k-1")
        val b = post("""{"url":"https://example.com/once"}""", key = "k-1")
        assertEquals(a.headers().firstValue("Location"), b.headers().firstValue("Location"))
    }

    @Test fun `clicks are counted after the queue flushes`() {
        val code = post("""{"url":"https://example.com/c"}""").headers().firstValue("Location").get()
        repeat(3) { get(code) }
        server.clicks.flush()                                   // eventual consistency, made deterministic
        assertTrue(""""clicks":3""" in get("/api/links$code/stats").body())
    }

    @Test fun `a cached redirect does not query the database`() {
        val code = post("""{"url":"https://example.com/hot"}""").headers().firstValue("Location").get()
        get(code)                                               // miss: loads into the cache
        val before = store.queries.get()
        repeat(5) { assertEquals(302, get(code).statusCode()) }
        assertEquals(before, store.queries.get())
    }
}
