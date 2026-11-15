import org.junit.Assert.assertEquals
import org.junit.Test

class DpTest {
    @Test fun `10 stairs by 1 or 2 has 89 ways`() {
        assertEquals(89L, ways(10, listOf(1, 2)))
    }

    @Test fun `edge cases for stairs`() {
        assertEquals("zero stairs: one way, don't move", 1L, ways(0, listOf(1, 2)))
        assertEquals("steps of 2 never land on stair 7", 0L, ways(7, listOf(2)))
    }

    @Test fun `big answers fit in a Long`() {
        assertEquals(37889062373143906L, ways(80, listOf(1, 2)))
    }

    @Test fun `DP beats greedy on 1 3 4 for 6`() {
        assertEquals("3 + 3, where greedy takes 4 + 1 + 1", 2, minCoins(listOf(1, 3, 4), 6))
    }

    @Test fun `zero needs no coins and an impossible amount gives -1`() {
        assertEquals(0, minCoins(listOf(5, 10), 0))
        assertEquals(-1, minCoins(listOf(2), 3))
    }

    @Test fun `the memo keeps a larger amount fast`() {
        assertEquals("25 + 25 + 25 + 10 + 10 + 1 + 1 + 1 + 1", 9, minCoins(listOf(1, 5, 10, 25), 99))
    }
}
