import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylistTest {
    @Test fun `add trims and stores songs in order`() {
        val p = Playlist(3)
        p.add("  Intro ")
        p.add("Outro")
        assertEquals(listOf("Intro", "Outro"), p.songs)
    }

    @Test fun `a full playlist refuses more songs`() {
        val p = Playlist(2)
        p.add("a")
        p.add("b")
        val e = runCatching { p.add("c") }.exceptionOrNull()
        assertTrue("the third add should throw IllegalStateException, not $e", e is IllegalStateException)
        assertEquals(listOf("a", "b"), p.songs)
    }

    @Test fun `the backing list is not visible outside`() {
        val getters = Playlist::class.java.methods.filter { it.name == "getItems" }
        assertTrue("items is still public: make it private", getters.isEmpty())
    }

    @Test fun `a cast to MutableList can't change the playlist`() {
        val p = Playlist(2)
        p.add("a")
        runCatching { (p.songs as? MutableList<String>)?.addAll(listOf("x", "y", "z")) }
        assertEquals("the playlist changed from outside", listOf("a"), p.songs)
    }

    @Test fun `a list you already got doesn't change later`() {
        val p = Playlist(3)
        p.add("a")
        val before = p.songs
        p.add("b")
        assertEquals(listOf("a"), before)
    }
}
