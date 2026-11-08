import org.junit.Assert.assertEquals
import org.junit.Test

class LabelTest {
    @Test fun `a book on its own gets the book label`() {
        assertEquals("book: Dune by Herbert", Book("Dune", "Herbert").label())
    }

    @Test fun `labels uses each item's real class`() {
        assertEquals(listOf("item: pen", "book: Dune by Herbert"), labels(listOf(Item("pen"), Book("Dune", "Herbert"))))
    }

    @Test fun `a book stored in an Item variable keeps its label`() {
        val i: Item = Book("Emma", "Austen")
        assertEquals("book: Emma by Austen", i.label())
    }

    @Test fun `bookCount counts only books`() {
        val shelf = listOf(Item("pen"), Book("Dune", "Herbert"), Item("lamp"), Book("Emma", "Austen"))
        assertEquals(2, shelf.bookCount)
    }

    @Test fun `an empty list has no books`() {
        assertEquals(0, emptyList<Item>().bookCount)
    }
}
