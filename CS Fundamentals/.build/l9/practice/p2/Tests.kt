import java.util.Properties
import org.junit.Assert.assertEquals
import org.junit.Test

class BoundaryTest {
    private fun config(vararg pairs: Pair<String, String>) =
        Properties().apply { for ((k, v) in pairs) setProperty(k, v) }

    @Test fun `port is read from the config`() {
        assertEquals(9090, port(config("port" to "9090")))
    }

    @Test fun `a missing port falls back to 8080`() {
        assertEquals(8080, port(config("host" to "example.org")))
    }

    @Test fun `a port that is not a number falls back to 8080`() {
        assertEquals(8080, port(config("port" to "eighty")))
    }

    @Test fun `folderOf gives the parent folder`() {
        assertEquals("/var/log", folderOf("/var/log/app.log"))
    }

    @Test fun `a bare file name has no parent, so folderOf gives a dot`() {
        assertEquals(".", folderOf("notes.txt"))
    }

    @Test fun `describe uses the name and whether the path is absolute`() {
        assertEquals("app.log (absolute)", describe("/var/log/app.log"))
        assertEquals("notes.txt (relative)", describe("notes.txt"))
    }
}
