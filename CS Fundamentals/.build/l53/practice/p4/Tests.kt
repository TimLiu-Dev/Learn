import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ExpandTest {
    private val vars = mapOf("f" to "my notes.txt", "dest" to "backup", "empty" to "")
    private val args = listOf("my notes.txt", "b.txt")

    @Test fun `a quoted variable stays one word`() {
        assertEquals(listOf("cp", "my notes.txt", "backup"), expand("cp \"\$f\" \"\$dest\"", vars, args))
    }

    @Test fun `an unquoted variable is split into words`() {
        assertEquals(listOf("rm", "my", "notes.txt"), expand("rm \$f", vars, args))
    }

    @Test fun `quoted dollar-at keeps each argument whole, unquoted forms split them`() {
        assertEquals("\"\$@\"", listOf("ls", "my notes.txt", "b.txt"), expand("ls \"\$@\"", vars, args))
        assertEquals("\$*", listOf("ls", "my", "notes.txt", "b.txt"), expand("ls \$*", vars, args))
        assertEquals("\$@", listOf("ls", "my", "notes.txt", "b.txt"), expand("ls \$@", vars, args))
    }

    @Test fun `an empty value is one empty word only when quoted`() {
        assertEquals("unquoted", listOf("ls"), expand("ls \$empty", vars, args))
        assertEquals("quoted", listOf("ls", ""), expand("ls \"\$empty\"", vars, args))
    }

    @Test fun `an unset variable is an error, as with set -u`() {
        try {
            expand("cd \"\$nope\"", vars, args)
            fail("expected an IllegalStateException for \$nope")
        } catch (e: IllegalStateException) {
            assertEquals("nope: unbound variable", e.message)
        }
    }
}
