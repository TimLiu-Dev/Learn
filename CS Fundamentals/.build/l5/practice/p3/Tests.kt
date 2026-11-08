import org.junit.Assert.assertEquals
import org.junit.Test

class GradeBookTest {
    @Test fun `the book starts with the given scores`() {
        assertEquals(listOf(70, 80), GradeBook(mutableListOf(70, 80)).all)
    }

    @Test fun `add still works`() {
        val book = GradeBook(mutableListOf(70, 80))
        book.add(90)
        assertEquals(listOf(70, 80, 90), book.all)
    }

    @Test fun `the caller's later changes don't reach the book`() {
        val start = mutableListOf(70, 80)
        val book = GradeBook(start)
        start.add(100)
        assertEquals(listOf(70, 80), book.all)
    }

    @Test fun `a list you got earlier doesn't change`() {
        val book = GradeBook(mutableListOf(70, 80))
        val earlier = book.all
        book.add(90)
        assertEquals("a read-only view would follow the book", listOf(70, 80), earlier)
    }

    @Test fun `casting back to MutableList can't change the book`() {
        val book = GradeBook(mutableListOf(70, 80))
        runCatching { (book.all as MutableList<Int>).add(0) }   // a read-only type is only a type
        assertEquals(listOf(70, 80), book.all)
    }
}
