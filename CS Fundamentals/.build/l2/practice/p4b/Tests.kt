import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class ChatCommandTest {
    @Test fun `command reads the word after the slash`() {
        assertEquals("mute", command("/mute @ada 10m"))
    }

    @Test fun `command ignores outer spaces and case`() {
        assertEquals("mute", command("  /MUTE @ada"))
    }

    @Test fun `text without a leading slash is not a command`() {
        assertNull("hello /mute", command("hello /mute"))
        assertNull("a lone slash", command("/"))
    }

    @Test fun `command works on a Turkish laptop`() {
        val saved = Locale.getDefault()
        Locale.setDefault(Locale.forLanguageTag("tr-TR"))
        try {
            assertEquals("quit", command("/QUIT"))
        } finally {
            Locale.setDefault(saved)
        }
    }

    @Test fun `seconds understands s, m and h`() {
        assertEquals(45, seconds("45s"))
        assertEquals(600, seconds("10m"))
        assertEquals(7200, seconds("2h"))
    }

    @Test fun `seconds rejects anything else`() {
        assertNull("10min", seconds("10min"))
        assertNull("m", seconds("m"))
        assertNull("-5m", seconds("-5m"))
    }
}
