import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadPortTest {
    @Test fun `a good line gives the port and closes the connection`() {
        val conn = Conn("app.conf", " 8080 ")
        assertEquals(8080, readPort(conn))
        assertTrue("connection should be closed", conn.closed)
    }

    @Test fun `a bad number becomes a ConfigException that names the source`() {
        val e = assertThrows(ConfigException::class.java) { readPort(Conn("app.conf", "80x")) }
        assertTrue("message should name the source, was: ${e.message}", e.message!!.contains("app.conf"))
    }

    @Test fun `the original NumberFormatException is kept as the cause`() {
        val e = assertThrows(ConfigException::class.java) { readPort(Conn("app.conf", "80x")) }
        assertTrue("cause should be a NumberFormatException, was: ${e.cause}", e.cause is NumberFormatException)
    }

    @Test fun `the connection is closed even when parsing fails`() {
        val conn = Conn("app.conf", "80x")
        assertThrows(ConfigException::class.java) { readPort(conn) }
        assertTrue("connection should be closed after a failure", conn.closed)
    }

    @Test fun `a dropped connection is not disguised as a config problem`() {
        val e = assertThrows(IllegalStateException::class.java) { readPort(Conn("app.conf", "DROP")) }
        assertEquals("connection app.conf dropped", e.message)
    }
}
