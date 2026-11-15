import org.junit.Assert.assertEquals
import org.junit.Test

// Shows CRLF as \r\n so a failure message stays on one line.
fun show(s: String) = s.replace("\r", "\\r").replace("\n", "\\n")

class ConditionalGetTest {
    val tag = "\"v7\""

    @Test fun `no If-None-Match gets the full 200`() {
        assertEquals(show("HTTP/1.1 200 OK\r\nETag: \"v7\"\r\nCache-Control: max-age=60\r\nContent-Length: 6\r\n\r\nhello\n"),
                     show(respond(null, tag, "max-age=60", "hello\n")))
    }

    @Test fun `a matching tag gets a 304 with no body`() {
        assertEquals(show("HTTP/1.1 304 Not Modified\r\nETag: \"v7\"\r\nCache-Control: max-age=60\r\n\r\n"),
                     show(respond("\"v7\"", tag, "max-age=60", "hello\n")))
    }

    @Test fun `a weak tag matches too`() {
        assertEquals("W/\"v7\"", true, clientHasIt("W/\"v7\"", tag))
    }

    @Test fun `any tag in a list may match`() {
        assertEquals("\"v5\", \"v7\"", true, clientHasIt("\"v5\", \"v7\"", tag))
        assertEquals("\"v5\",\"v6\"", false, clientHasIt("\"v5\",\"v6\"", tag))
        assertEquals("a quoted tag is not the bare name", false, clientHasIt("v7", tag))
    }

    @Test fun `a star matches whatever version exists`() {
        assertEquals("*", true, clientHasIt("*", tag))
    }

    @Test fun `Content-Length counts bytes, not characters`() {
        val r = respond(null, tag, "no-cache", "café\n")
        assertEquals("é is 2 bytes in UTF-8", "Content-Length: 6", r.lines().first { it.startsWith("Content-Length") }.trim())
    }
}
