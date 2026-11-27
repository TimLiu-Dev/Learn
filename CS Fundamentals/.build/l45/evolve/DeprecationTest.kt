package shelf

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

// Exercise 5: retire GET /members/{id}/loans without breaking the clients that still call it.
class DeprecationTest {
    private val dbFile = Files.createTempFile("shelf-deprecation", ".db")
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)
    private val shelf = Shelf(dbFile, 0, clock).start()
    private val client = HttpClient.newHttpClient()

    @AfterTest
    fun tearDown() {
        shelf.close()
        Files.deleteIfExists(dbFile)
    }

    private fun call(method: String, path: String, body: String? = null): HttpResponse<String> {
        val publisher = body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody()
        val request = HttpRequest.newBuilder(URI("http://127.0.0.1:${shelf.port}$path"))
            .method(method, publisher).header("Content-Type", "application/json").build()
        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    @Test
    fun `the old route answers as before and announces its retirement`() {
        call("POST", "/books", """{"title":"Dune","author":"Frank Herbert"}""")
        call("POST", "/members", """{"name":"Ada","email":"ada@example.com"}""")
        call("POST", "/loans", """{"bookId":1,"memberId":1}""")
        val old = call("GET", "/members/1/loans")
        val new = call("GET", "/loans?member=1")
        assertEquals(200, old.statusCode())
        assertEquals(new.body(), old.body())                       // same data, so clients can switch at leisure
        assertEquals("@1798761600", old.headers().firstValue("Deprecation").get())
        assertEquals("Sat, 01 Jan 2028 00:00:00 GMT", old.headers().firstValue("Sunset").get())
        assertFalse(new.headers().firstValue("Deprecation").isPresent)
    }

    @Test
    fun `the successor needs a member`() {
        assertEquals(400, call("GET", "/loans").statusCode())
        assertEquals(404, call("GET", "/loans?member=7").statusCode())
    }
}
