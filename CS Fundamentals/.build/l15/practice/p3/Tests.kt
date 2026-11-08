import org.junit.Assert.assertEquals
import org.junit.Test

class TrackedTest {
    @Test fun `a fresh draft is clean`() {
        val d = Draft()
        assertEquals("", d.title)
        assertEquals(0, d.words)
        assertEquals(emptySet<String>(), d.dirty)
    }

    @Test fun `a new value is stored and marked dirty`() {
        val d = Draft()
        d.title = "Lazy pipelines"
        d.words = 120
        assertEquals("Lazy pipelines", d.title)
        assertEquals(120, d.words)
        assertEquals(setOf("title", "words"), d.dirty)
    }

    @Test fun `assigning the same value is not a change`() {
        val d = Draft()
        d.body = ""
        d.words = 0
        assertEquals("dirty after assigning the initial values", emptySet<String>(), d.dirty)
    }

    @Test fun `save lists the changes sorted and starts clean`() {
        val d = Draft()
        d.title = "T"
        d.body = "B"
        assertEquals("saved body, title", d.save())
        assertEquals("nothing to save", d.save())
        d.title = "T"
        assertEquals("same title again", "nothing to save", d.save())
    }

    @Test fun `two drafts don't share values`() {
        val a = Draft()
        val b = Draft()
        a.title = "A"
        assertEquals("b's title", "", b.title)
        assertEquals("b's dirty set", emptySet<String>(), b.dirty)
    }
}
