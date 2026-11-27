import org.junit.Assert.assertEquals
import org.junit.Test

// A filter that records when the request passes it on the way in and on the way out.
class Trace(private val name: String, private val events: MutableList<String>) : Filter {
    override fun filter(req: Request, next: Handler): Response {
        events += "$name in"
        val res = next.handle(req)
        events += "$name out"
        return res
    }
}

class FilterChainTest {
    private val ok = Handler { Response(200) }

    @Test fun `with no filters the handler answers`() {
        assertEquals(200, chain(emptyList(), ok).handle(Request("ann", "/books")).status)
    }

    @Test fun `filters run in the order given and unwind in reverse`() {
        val events = mutableListOf<String>()
        val h = chain(listOf(Trace("A", events), Trace("B", events))) { events += "handler"; Response(200) }
        h.handle(Request("ann", "/books"))
        assertEquals(listOf("A in", "B in", "handler", "B out", "A out"), events)
    }

    @Test fun `a refused request never reaches the handler`() {
        var calls = 0
        val h = chain(listOf(RateLimit { it != "mallory" })) { calls++; Response(200) }
        val res = h.handle(Request("mallory", "/books"))
        assertEquals(429, res.status)
        assertEquals("1", res.headers["Retry-After"])
        assertEquals("handler calls", 0, calls)
    }

    @Test fun `an allowed request passes through`() {
        val h = chain(listOf(RateLimit { true }), ok)
        assertEquals(200, h.handle(Request("ann", "/books")).status)
    }

    @Test fun `the access log goes first, so it sees the 429s`() {
        val lines = mutableListOf<String>()
        val h = chain(listOf(AccessLog(lines), RateLimit { it != "mallory" }), ok)
        h.handle(Request("ann", "/books"))
        h.handle(Request("mallory", "/books"))
        assertEquals(listOf("ann /books 200", "mallory /books 429"), lines)
    }

    @Test fun `put the log after the limiter and the 429s vanish from it`() {
        val lines = mutableListOf<String>()
        val h = chain(listOf(RateLimit { it != "mallory" }, AccessLog(lines)), ok)
        h.handle(Request("ann", "/books"))
        h.handle(Request("mallory", "/books"))
        assertEquals(listOf("ann /books 200"), lines)
    }
}
