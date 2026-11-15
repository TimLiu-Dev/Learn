import java.lang.reflect.Modifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HoldsTest {
    @Test fun `first come, first served`() {
        val h = Holds()
        assertEquals("Ada's place", 1, h.place("Dune", "Ada"))
        assertEquals("Linus's place", 2, h.place("Dune", "Linus"))
        assertEquals(listOf("Ada", "Linus"), h.waiting("Dune"))
        assertEquals("Ada", h.next("Dune"))
        assertEquals(listOf("Linus"), h.waiting("Dune"))
    }

    @Test fun `placing twice keeps your first place`() {
        val h = Holds()
        h.place("Dune", "Ada")
        h.place("Dune", "Linus")
        assertEquals("Ada asks again", 1, h.place("Dune", "Ada"))
        assertEquals(listOf("Ada", "Linus"), h.waiting("Dune"))
    }

    @Test fun `next and cancel have no error cases`() {
        val h = Holds()
        assertNull("nobody waits for Emma", h.next("Emma"))
        h.cancel("Emma", "Ada")                  // nothing to cancel: must not throw
        h.place("Dune", "Ada")
        h.cancel("Dune", "Ada")
        assertNull("the line is empty again", h.next("Dune"))
        assertEquals(emptyList<String>(), h.waiting("Dune"))
    }

    @Test fun `waiting can't be used to change the queue`() {
        val h = Holds()
        h.place("Dune", "Ada")
        val seen = h.waiting("Dune")
        runCatching { (seen as MutableList<String>).add("Eve") }   // a caller cheating
        assertEquals(listOf("Ada"), h.waiting("Dune"))
    }

    @Test fun `only the four operations are public`() {
        val public = Holds::class.java.declaredMethods
            .filter { Modifier.isPublic(it.modifiers) && !it.isSynthetic }
            .map { it.name }.toSet()
        assertEquals(setOf("place", "next", "cancel", "waiting"), public)
    }
}
