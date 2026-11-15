import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EnvTest {
    @Test fun `a value may itself contain equals signs`() {
        assertEquals(mapOf("JAVA_OPTS" to "-Dmode=fast -Dlevel=2"), parseEnv(listOf("JAVA_OPTS=-Dmode=fast -Dlevel=2")))
    }

    @Test fun `blank lines, comments and lines without equals are skipped`() {
        val env = parseEnv(listOf("# settings", "", "HOME=/Users/you", "not a variable", "EMPTY="))
        assertEquals(mapOf("HOME" to "/Users/you", "EMPTY" to ""), env)
    }

    @Test fun `a later line wins`() {
        assertEquals("debug", parseEnv(listOf("LEVEL=info", "LEVEL=debug"))["LEVEL"])
    }

    @Test fun `the first match in PATH wins`() {
        val env = parseEnv(listOf("PATH=/Users/you/fakebin:/usr/bin:/bin"))
        val files = setOf("/usr/bin/java", "/Users/you/fakebin/java", "/bin/ls")
        assertEquals("/Users/you/fakebin/java", which("java", env, files))
        assertEquals("/bin/ls", which("ls", env, files))
    }

    @Test fun `no match, or no PATH at all, gives null`() {
        val files = setOf("/usr/bin/java")
        assertNull("not on PATH", which("kotlinc", mapOf("PATH" to "/usr/bin:/bin"), files))
        assertNull("PATH unset", which("java", emptyMap(), files))
    }
}
