import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class RequestDslTest {
    @Test fun `the block configures the request`() {
        val r = request { method = "POST"; url = "/books"; header("Accept", "json") }
        assertEquals("POST", r.method)
        assertEquals("/books", r.url)
        assertEquals(mapOf("Accept" to "json"), r.headers)
    }

    @Test fun `method defaults to GET`() {
        assertEquals("GET", request { url = "/books" }.method)
    }

    @Test fun `a repeated header replaces the value but keeps its place`() {
        val r = request {
            url = "/a"
            header("A", "1")
            header("B", "2")
            header("A", "3")
        }
        assertEquals("headers in order", listOf("A" to "3", "B" to "2"), r.headers.toList())
    }

    @Test fun `a blank url is rejected`() {
        try {
            request { url = "   " }
            fail("expected IllegalArgumentException for a blank url")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }

    @Test fun `a built request doesn't change when the builder is reused`() {
        val builder = RequestBuilder().apply { url = "/a"; header("A", "1") }
        val first = builder.build()
        builder.header("B", "2")
        assertEquals("first request's headers", mapOf("A" to "1"), first.headers)
    }
}
