import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// A plain message: the page's test view hides text written inside <…>, which assertEquals uses.
fun assertText(expected: String, actual: String?, what: String = "") =
    assertTrue("$what expected \"$expected\" but was \"$actual\"".trim(), expected == actual)

class BadgeTest {
    val roster = listOf(
        "Ada Lovelace", "Alan Turing", "Barbara Liskov", "Edsger Dijkstra",
        "Frances Allen", "Grace  Hopper", "Donald Knuth", "John McCarthy",
        "Katherine Johnson", " Linus Torvalds", "Margaret Hamilton", "Tim Berners Lee",
    )

    @Test fun `two words give two initials`() {
        assertText("AL", initials("Ada Lovelace"))
    }

    @Test fun `lower-case words are capitalised`() {
        assertText("GH", initials("grace hopper"))
    }

    @Test fun `one word gives one initial`() {
        assertText("C", initials("Cher"))
    }

    @Test fun `every name on the roster gets a badge`() {
        val expected = listOf("AL", "AT", "BL", "ED", "FA", "GH", "DK", "JM", "KJ", "LT", "MH", "TBL")
        assertEquals(expected, badges(roster))
    }

    @Test fun `a blank name gives an empty badge`() {
        assertText("", initials(""))
        assertText("", initials("   "))
    }
}
