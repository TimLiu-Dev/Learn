package books

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json

class BookApiTest {
    private val api = BookApi(port = 0)                      // port 0: the OS picks a free port
    private val client = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeTest fun start() = api.start()
    @AfterTest fun stop() = api.stop()

    private fun call(method: String, path: String, body: String? = null): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI("http://127.0.0.1:${api.port}$path"))
            .header("Content-Type", "application/json")
            .method(method, body?.let { BodyPublishers.ofString(it) } ?: BodyPublishers.noBody())
            .build()
        return client.send(request, BodyHandlers.ofString())
    }

    private fun create(title: String, year: Int) = call("POST", "/books", """{"title":"$title","year":$year}""")

    @Test fun `POST creates a book with 201 and a Location header`() {
        val res = create("Dune", 1965)
        assertEquals(201, res.statusCode())
        assertEquals("/books/1", res.headers().firstValue("Location").orElse(null))
        assertEquals(Book(1, "Dune", 1965), json.decodeFromString<Book>(res.body()))
    }

    @Test fun `GET returns the book, or a 404 problem`() {
        create("Dune", 1965)
        assertEquals(200, call("GET", "/books/1").statusCode())
        val missing = call("GET", "/books/99")
        assertEquals(404, missing.statusCode())
        assertEquals("application/problem+json", missing.headers().firstValue("Content-Type").get())
        assertEquals("Not Found", json.decodeFromString<Problem>(missing.body()).title)
    }

    @Test fun `invalid input lists every error`() {
        val res = call("POST", "/books", """{"title":"  ","year":3000}""")
        assertEquals(400, res.statusCode())
        val problem = json.decodeFromString<Problem>(res.body())
        assertEquals(listOf("title must not be blank", "year must be between 1440 and 2100"), problem.errors)
    }

    @Test fun `malformed JSON is a 400, not a 500`() {
        assertEquals(400, call("POST", "/books", """{"title": "Dune", """).statusCode())
        assertEquals(400, call("POST", "/books", """{"title":"Dune","year":"old"}""").statusCode())
    }

    @Test fun `list pages through books with a next link`() {
        listOf("A", "B", "C").forEachIndexed { i, t -> create(t, 2000 + i) }
        val first = json.decodeFromString<Page>(call("GET", "/books?limit=2").body())
        assertEquals(listOf("A", "B"), first.items.map { it.title })
        assertEquals("/books?limit=2&offset=2", first.next)
        val last = json.decodeFromString<Page>(call("GET", first.next!!).body())
        assertEquals(listOf("C"), last.items.map { it.title })
        assertNull(last.next)
        assertEquals(400, call("GET", "/books?limit=0").statusCode())
    }

    @Test fun `PUT replaces and DELETE removes`() {
        create("Dune", 1965)
        val put = call("PUT", "/books/1", """{"title":"Dune Messiah","year":1969}""")
        assertEquals(200, put.statusCode())
        assertEquals("Dune Messiah", json.decodeFromString<Book>(put.body()).title)
        assertEquals(204, call("DELETE", "/books/1").statusCode())
        assertEquals(404, call("DELETE", "/books/1").statusCode())   // already gone
    }

    @Test fun `unsupported method is 405 with an Allow header`() {
        val res = call("PATCH", "/books")
        assertEquals(405, res.statusCode())
        assertEquals("GET, POST", res.headers().firstValue("Allow").get())
    }
}
