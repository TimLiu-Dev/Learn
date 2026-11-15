import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

fun bookRouter() = Router().apply {
    add("GET", "/books") { Response(200, "all books") }
    add("POST", "/books") { Response(201, "created") }
    add("GET", "/books/{id}") { Response(200, "book ${it["id"]}") }
    add("PUT", "/books/{id}") { Response(200, "replaced ${it["id"]}") }
    add("GET", "/books/{id}/loans/{loan}") { Response(200, "loan ${it["loan"]} of book ${it["id"]}") }
}

class RouterTest {
    @Test fun `match captures path parameters`() {
        assertEquals(mapOf("id" to "42"), match("/books/{id}", "/books/42"))
        assertEquals(mapOf<String, String>(), match("/books", "/books"))
        assertNull("a different literal segment", match("/books", "/authors"))
    }

    @Test fun `a request reaches its handler with its parameters`() {
        val r = bookRouter()
        assertEquals(Response(200, "all books"), r.handle("GET", "/books"))
        assertEquals(Response(200, "book 42"), r.handle("GET", "/books/42"))
        assertEquals(Response(200, "loan 3 of book 7"), r.handle("GET", "/books/7/loans/3"))
    }

    @Test fun `an unknown path is 404`() {
        assertEquals("GET /authors", 404, bookRouter().handle("GET", "/authors").status)
    }

    @Test fun `prefixes and extra segments are not matches`() {
        val r = bookRouter()
        assertEquals("GET /booksXYZ", 404, r.handle("GET", "/booksXYZ").status)
        assertEquals("GET /books/42/extra", 404, r.handle("GET", "/books/42/extra").status)
        assertEquals("GET /books//loans/3 (empty id)", 404, r.handle("GET", "/books//loans/3").status)
    }

    @Test fun `a known path with the wrong method is 405 with Allow`() {
        val res = bookRouter().handle("PATCH", "/books")
        assertEquals("PATCH /books status", 405, res.status)
        assertEquals("Allow for /books", "GET, POST", res.allow)
    }

    @Test fun `Allow lists only the methods of the matching path`() {
        assertEquals("Allow for /books/7", "GET, PUT", bookRouter().handle("DELETE", "/books/7").allow)
    }
}
