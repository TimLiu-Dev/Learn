import org.junit.Assert.assertEquals
import org.junit.Test

class ExitStatusTest {
    private val log = listOf(
        "09:00:01 GET /books 200 12ms",
        "09:00:02 GET /books/7 500 31ms",
        "09:00:03 POST /books 201 500ms",
        "09:00:04 GET /health 500 2ms",
    )

    @Test fun `grep -c counts the lines that contain the pattern`() {
        assertEquals(GrepResult(2, 0), grepC(Regex(" 500 "), log))
    }

    @Test fun `without the spaces, 500 also matches a latency`() {
        assertEquals(GrepResult(3, 0), grepC(Regex("500"), log))
    }

    @Test fun `no match prints 0 and exits 1`() {
        assertEquals(GrepResult(0, 1), grepC(Regex(" 404 "), log))
        assertEquals("empty input", GrepResult(0, 1), grepC(Regex("x"), emptyList()))
    }

    @Test fun `an exit code passes straight through`() {
        assertEquals(0, status(Exited(0)))
        assertEquals(2, status(Exited(2)))
    }

    @Test fun `a signal gives 128 plus its number`() {
        assertEquals("SIGTERM", 143, status(Signalled("TERM")))
        assertEquals("SIGINT", 130, status(Signalled("INT")))
        assertEquals("SIGKILL", 137, status(Signalled("KILL")))
        assertEquals("SIGPIPE", 141, status(Signalled("PIPE")))
    }
}
