import org.junit.Assert.assertEquals
import org.junit.Test

class BadgeTest {
    val roster = listOf(
        "Ada Lovelace", "Alan Turing", "Barbara Liskov", "Edsger Dijkstra",
        "Frances Allen", "Grace  Hopper", "Donald Knuth", "John McCarthy",
        "Katherine Johnson", " Linus Torvalds", "Margaret Hamilton", "Tim Berners Lee",
    )

    @Test fun `two words give two initials`() {
        assertEquals("AL", initials("Ada Lovelace"))
    }

    @Test fun `lower-case words are capitalised`() {
        assertEquals("GH", initials("grace hopper"))
    }

    @Test fun `one word gives one initial`() {
        assertEquals("C", initials("Cher"))
    }

    @Test fun `every name on the roster gets a badge`() {
        val expected = listOf("AL", "AT", "BL", "ED", "FA", "GH", "DK", "JM", "KJ", "LT", "MH", "TBL")
        assertEquals(expected, badges(roster))
    }

    @Test fun `a blank name gives an empty badge`() {
        assertEquals("", initials(""))
        assertEquals("", initials("   "))
    }
}
