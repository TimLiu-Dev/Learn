import java.io.IOException
import java.lang.reflect.Modifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class JavaViewTest {
    private val playlist = Playlist::class.java

    @Test fun `Java reads p dot name as a public field`() {
        val field = try { playlist.getField("name") } catch (e: NoSuchFieldException) { null }
        assertNotNull("Java sees only getName(), no public field called name", field)
    }

    @Test fun `Java can call add with just a title`() {
        val add = try { playlist.getMethod("add", String::class.java) } catch (e: NoSuchMethodException) { null }
        assertNotNull("Java sees only add(String, int)", add)
    }

    @Test fun `Java may catch IOException from load`() {
        val load = playlist.getMethod("load", String::class.java)
        assertEquals("load's throws clause, as Java sees it", listOf(IOException::class.java), load.exceptionTypes.toList())
    }

    @Test fun `Java calls Library dot register without INSTANCE`() {
        val register = Library::class.java.getMethod("register", Playlist::class.java)
        assertTrue("register must be a static method on Library", Modifier.isStatic(register.modifiers))
    }

    @Test fun `Kotlin callers still work the same way`() {
        val p = Playlist("road trip")
        p.add("Hey Jude")
        p.add("Help", times = 2)
        p.load("Yesterday\nLet It Be")
        assertEquals("tracks", 5, p.size)
        val before = Library.count
        Library.register(p)
        assertEquals("registered playlists", before + 1, Library.count)
    }

    @Test fun `loading nothing still throws IOException`() {
        try {
            Playlist("empty").load("  ")
            fail("expected an IOException")
        } catch (e: IOException) {
            assertEquals("nothing to load", e.message)
        }
    }
}
