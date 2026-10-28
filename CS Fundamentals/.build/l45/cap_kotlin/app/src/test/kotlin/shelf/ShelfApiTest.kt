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
import kotlin.test.assertTrue

// Integration tests: the whole app on a real port, a real SQLite file, real HTTP.
class ShelfApiTest {
    private val dbFile = Files.createTempFile("shelf-api", ".db")
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)
    private val shelf = Shelf(dbFile, 0, clock).start()                 // port 0: the OS picks a free port
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

    private fun addBook(title: String, author: String = "Frank Herbert") =
        call("POST", "/books", """{"title":"$title","author":"$author","year":1965}""")

    @Test
    fun `health is UP`() {
        assertEquals("""{"status":"UP"}""", call("GET", "/health").body())
    }

    @Test
    fun `creating a book returns 201, a Location and the book`() {
        val r = addBook("Dune")
        assertEquals(201, r.statusCode())
        assertEquals("/books/1", r.headers().firstValue("Location").get())
        assertEquals("""{"id":1,"title":"Dune","author":"Frank Herbert","year":1965}""", r.body())
        assertEquals(r.body(), call("GET", "/books/1").body())
    }

    @Test
    fun `invalid input is a 400 problem listing every error`() {
        val r = call("POST", "/books", """{"title":" ","year":3000}""")
        assertEquals(400, r.statusCode())
        assertEquals("application/problem+json", r.headers().firstValue("Content-Type").get())
        assertTrue("title is required" in r.body() && "author is required" in r.body() && "year must be" in r.body())
        assertEquals(400, call("POST", "/books", """{"title": oops""").statusCode())
    }

    @Test
    fun `unknown things are 404 and wrong methods are 405 with Allow`() {
        assertEquals(404, call("GET", "/books/42").statusCode())
        assertEquals(404, call("GET", "/books/abc").statusCode())
        assertEquals(404, call("GET", "/authors").statusCode())
        val r = call("PATCH", "/books/1")
        assertEquals(405, r.statusCode())
        assertEquals("DELETE, GET, PUT", r.headers().firstValue("Allow").get())
    }

    @Test
    fun `lists are paged with a next link`() {
        repeat(3) { addBook("Book $it") }
        val r = call("GET", "/books?limit=2")
        assertTrue(""""total":3""" in r.body() && """"next":"/books?limit=2&offset=2"""" in r.body(), r.body())
        assertEquals(400, call("GET", "/books?limit=0").statusCode())
    }

    @Test
    fun `lending, returning and lending again`() {
        addBook("Dune")
        call("POST", "/members", """{"name":"Ada","email":"ada@example.com"}""")
        val lent = call("POST", "/loans", """{"bookId":1,"memberId":1}""")
        assertEquals(201, lent.statusCode())
        assertEquals("""{"id":1,"bookId":1,"memberId":1,"loaned":"2026-10-27"}""", lent.body())
        assertEquals(409, call("POST", "/loans", """{"bookId":1,"memberId":1}""").statusCode())
        val returned = call("POST", "/loans/1/return")
        assertEquals("""{"id":1,"bookId":1,"memberId":1,"loaned":"2026-10-27","returned":"2026-10-27"}""", returned.body())
        assertEquals(409, call("POST", "/loans/1/return").statusCode())
        assertEquals(201, call("POST", "/loans", """{"bookId":1,"memberId":1}""").statusCode())
        assertTrue(call("GET", "/members/1/loans").body().startsWith("""[{"id":1"""))
    }

    @Test
    fun `the business rules come back as 404 and 409`() {
        repeat(4) { addBook("Book $it") }
        call("POST", "/members", """{"name":"Ada","email":"ada@example.com"}""")
        assertEquals(409, call("POST", "/members", """{"name":"Ada 2","email":"ada@example.com"}""").statusCode())
        assertEquals(404, call("POST", "/loans", """{"bookId":9,"memberId":1}""").statusCode())
        (1..3).forEach { assertEquals(201, call("POST", "/loans", """{"bookId":$it,"memberId":1}""").statusCode()) }
        val fourth = call("POST", "/loans", """{"bookId":4,"memberId":1}""")
        assertEquals(409, fourth.statusCode())
        assertTrue("already has 3 open loans" in fourth.body())
    }

    @Test
    fun `a lent book cannot be deleted, a fresh one can`() {
        addBook("Dune"); addBook("Emma", "Jane Austen")
        call("POST", "/members", """{"name":"Ada","email":"ada@example.com"}""")
        call("POST", "/loans", """{"bookId":1,"memberId":1}""")
        assertEquals(409, call("DELETE", "/books/1").statusCode())
        assertEquals(204, call("DELETE", "/books/2").statusCode())
        assertEquals(404, call("GET", "/books/2").statusCode())
    }
}
