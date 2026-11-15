import org.junit.Assert.assertEquals
import org.junit.Test

class FirstErrorTest {
    @Test fun `balanced text gives -1`() {
        for (s in listOf("", "no brackets", "{[()]}", "f(a[0], {b})"))
            assertEquals("for \"$s\"", -1, firstError(s))
    }

    @Test fun `a closer with nothing open is the culprit`() {
        assertEquals("for \")\"", 0, firstError(")"))
        assertEquals("for \"())\"", 2, firstError("())"))
    }

    @Test fun `a closer of the wrong kind is the culprit`() {
        assertEquals("for \"(]\"", 1, firstError("(]"))
        assertEquals("for \"([)]\"", 2, firstError("([)]"))
    }

    @Test fun `a single opener left over is the culprit`() {
        assertEquals("for \"(\"", 0, firstError("("))
        assertEquals("for \"a(b[c]\"", 1, firstError("a(b[c]"))
    }

    @Test fun `with several left over, the leftmost is the culprit`() {
        assertEquals("for \"((\"", 0, firstError("(("))
        assertEquals("for \"x{[()\"", 1, firstError("x{[()"))
    }

    @Test fun `a bad closer is reported before leftovers`() {
        assertEquals("for \"((]\"", 2, firstError("((]"))
        assertEquals("for \"x)(\"", 1, firstError("x)("))
    }
}
