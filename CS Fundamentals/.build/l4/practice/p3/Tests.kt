import org.junit.Assert.assertEquals
import org.junit.Test

class PassTest {
    @Test fun `swapCounts swaps the numbers inside the objects`() {
        val x = Counter(1)
        val y = Counter(2)
        swapCounts(x, y)
        assertEquals("x.count", 2, x.count)
        assertEquals("y.count", 1, y.count)
    }

    @Test fun `swapping a counter with itself keeps its count`() {
        val x = Counter(5)
        swapCounts(x, x)
        assertEquals(5, x.count)
    }

    @Test fun `topThree finds the three highest`() {
        assertEquals(listOf(9, 7, 5), topThree(mutableListOf(5, 1, 9, 7, 3)))
    }

    @Test fun `topThree leaves the caller's list alone`() {
        val scores = mutableListOf(5, 1, 9, 7, 3)
        topThree(scores)
        assertEquals("the caller's list after the call", listOf(5, 1, 9, 7, 3), scores)
    }

    @Test fun `fewer than three scores is fine`() {
        assertEquals(listOf(8, 4), topThree(mutableListOf(4, 8)))
    }
}
