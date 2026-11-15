import org.junit.Assert.assertEquals
import org.junit.Test

class BookLinesTest {
    private val shelf = listOf(
        Book("Dune", "Herbert", 1965),
        Book("Emma", "Austen", 1815),
        Book("Ubik", "Dick", 1969),
    )

    @Test fun `bookLines still gives the same output in both modes`() {
        assertEquals(listOf("Austen: Emma", "Dick: Ubik", "Herbert: Dune"), bookLines(shelf, true))
        assertEquals(listOf("Emma (1815)", "Dune (1965)", "Ubik (1969)"), bookLines(shelf, false))
    }

    @Test fun `authorLines sorts by author`() {
        assertEquals(listOf("Austen: Emma", "Dick: Ubik", "Herbert: Dune"), authorLines(shelf))
    }

    @Test fun `yearLines lists the oldest first`() {
        assertEquals(listOf("Emma (1815)", "Dune (1965)", "Ubik (1969)"), yearLines(shelf))
    }

    @Test fun `books from the same year keep their shelf order`() {
        val sameYear = listOf(Book("Ubik", "Dick", 1969), Book("Kindred", "Butler", 1979), Book("Solaris", "Lem", 1961), Book("Slaughterhouse-Five", "Vonnegut", 1969))
        assertEquals(
            "a stable sort keeps Ubik before Slaughterhouse-Five",
            listOf("Solaris (1961)", "Ubik (1969)", "Slaughterhouse-Five (1969)", "Kindred (1979)"),
            yearLines(sameYear),
        )
    }

    @Test fun `an empty shelf gives no lines`() {
        assertEquals(emptyList<String>(), authorLines(emptyList()))
        assertEquals(emptyList<String>(), yearLines(emptyList()))
    }
}
