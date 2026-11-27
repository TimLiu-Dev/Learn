import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class SettingsTest {
    private val defaults = mapOf("PORT" to "8080", "POOL" to "8", "CACHE" to "1000")

    @Test fun `env beats the file, and the file beats the defaults`() {
        val s = loadSettings(
            env = mapOf("PORT" to "9000"),
            file = mapOf("PORT" to "8081", "POOL" to "4"),
            defaults = defaults, required = emptySet(), secrets = emptySet())
        assertEquals("PORT", "9000", s["PORT"])
        assertEquals("POOL", "4", s["POOL"])
        assertEquals("CACHE", "1000", s["CACHE"])
    }

    @Test fun `a key from any layer is kept`() {
        val s = loadSettings(mapOf("A" to "1"), mapOf("B" to "2"), mapOf("C" to "3"), emptySet(), emptySet())
        assertEquals(listOf("1", "2", "3"), listOf(s["A"], s["B"], s["C"]))
    }

    @Test fun `missing required keys fail fast, and the message names all of them`() {
        val e = assertThrows(MissingConfig::class.java) {
            loadSettings(emptyMap(), emptyMap(), defaults, setOf("DB_URL", "PORT", "DB_PASSWORD"), emptySet())
        }
        assertEquals("missing config: DB_PASSWORD, DB_URL", e.message)
    }

    @Test fun `a required key may come from any layer`() {
        val s = loadSettings(emptyMap(), mapOf("DB_URL" to "jdbc:sqlite:shelf.db"), defaults, setOf("DB_URL", "PORT"), emptySet())
        assertEquals("jdbc:sqlite:shelf.db", s["DB_URL"])
    }

    @Test fun `toString is sorted and never shows a secret`() {
        val s = loadSettings(
            env = mapOf("DB_PASSWORD" to "hunter2", "PORT" to "9000"),
            file = emptyMap(), defaults = mapOf("POOL" to "8"),
            required = setOf("DB_PASSWORD"), secrets = setOf("DB_PASSWORD"))
        assertEquals("DB_PASSWORD=***, POOL=8, PORT=9000", s.toString())
        assertFalse("the secret leaked", s.toString().contains("hunter2"))
        assertEquals("the code can still read it", "hunter2", s["DB_PASSWORD"])
    }
}
