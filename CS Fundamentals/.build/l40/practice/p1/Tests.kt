import org.junit.Assert.assertEquals
import org.junit.Test

class HunkHeaderTest {
    @Test fun `context lines count for both files`() {
        val body = listOf(" 3", " 4", " 5", "+NEW", " 6", " 7", " 8")
        assertEquals("@@ -3,6 +3,7 @@", hunkHeader(3, 3, body))
    }

    @Test fun `the lesson's AI diff hunk adds up`() {
        val body = List(11) { " context $it" } + List(25) { "+added $it" }
        assertEquals("@@ -4,11 +4,36 @@", hunkHeader(4, 4, body))
    }

    @Test fun `a one-line change leaves out the counts`() {
        assertEquals("@@ -1 +1 @@", hunkHeader(1, 1, listOf("-a", "+b")))
    }

    @Test fun `a no-newline marker counts for neither file`() {
        val body = listOf(" x", "-y", "\\ No newline at end of file", "+z", "\\ No newline at end of file")
        assertEquals("@@ -1,2 +1,2 @@", hunkHeader(1, 1, body))
    }

    @Test fun `a pure insertion has an empty old range`() {
        assertEquals("@@ -5,0 +6 @@", hunkHeader(5, 6, listOf("+NEW")))
    }
}
