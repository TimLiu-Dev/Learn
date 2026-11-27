import org.junit.Assert.assertEquals
import org.junit.Test

class SseParserTest {
    @Test fun `one event with the default type`() {
        assertEquals(listOf(Event("message", "hello", null)), parseSse("data: hello\n\n"))
    }

    @Test fun `several data lines join with a newline`() {
        // The WHATWG spec's stock-ticker example
        assertEquals(listOf(Event("message", "YHOO\n+2\n10", null)), parseSse("data: YHOO\ndata: +2\ndata: 10\n\n"))
    }

    @Test fun `comments are ignored and the type resets after each event`() {
        val got = parseSse(": keep-alive\n\nevent: delta\ndata: Hel\n\ndata: lo\n\n")
        assertEquals(listOf(Event("delta", "Hel", null), Event("message", "lo", null)), got)
    }

    @Test fun `only one leading space is removed`() {
        assertEquals("\" two spaces\\nnone\"", listOf(Event("message", " two spaces\nnone", null)),
                     parseSse("data:  two spaces\ndata:none\n\n"))
    }

    @Test fun `the last id carries over to later events`() {
        assertEquals(listOf(Event("message", "a", "7"), Event("message", "b", "7")),
                     parseSse("id: 7\ndata: a\n\ndata: b\n\n"))
    }

    @Test fun `no data means no event, and an unfinished event is dropped`() {
        assertEquals(emptyList<Event>(), parseSse("event: ping\n\ndata: last"))
    }

    @Test fun `CRLF line endings work too`() {
        assertEquals(listOf(Event("message", "x", null)), parseSse("data: x\r\n\r\n"))
    }
}
