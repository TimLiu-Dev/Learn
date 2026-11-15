import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResizingStackTest {
    private fun stackOf(n: Int) = ResizingStack<Int>().apply { for (i in 1..n) push(i) }

    @Test fun `pop returns elements in reverse order`() {
        val s = stackOf(3)
        assertEquals(listOf(3, 2, 1), List(3) { s.pop() })
        assertEquals(0, s.size)
    }

    @Test(expected = NoSuchElementException::class)
    fun `pop on an empty stack throws NoSuchElementException`() {
        ResizingStack<String>().pop()
    }

    @Test fun `halves when a quarter full, not before`() {
        val s = stackOf(8)
        assertEquals(8, s.capacity)
        repeat(5) { s.pop() }
        assertEquals("size 3 of 8: no shrink yet", 8, s.capacity)
        s.pop()
        assertEquals("size 2 is a quarter of 8: halve", 4, s.capacity)
    }

    @Test fun `elements survive a shrink`() {
        val s = stackOf(16)
        repeat(12) { s.pop() }
        assertEquals(8, s.capacity)
        assertEquals(listOf(4, 3, 2, 1), List(4) { s.pop() })
    }

    @Test fun `push and pop at a boundary don't thrash`() {
        val s = stackOf(8)                                 // full: the next push doubles to 16
        val before = s.copies
        repeat(1_000) { s.push(0); s.pop() }
        val added = s.copies - before
        assertTrue("1,000 push/pop pairs copied $added elements: each pair is resizing", added < 100)
    }

    @Test fun `a long run copies fewer than 3 elements per operation`() {
        val s = stackOf(10_000)
        repeat(10_000) { s.pop() }
        assertTrue("copied ${s.copies} elements for 20,000 operations", s.copies < 3 * 20_000)
    }
}
