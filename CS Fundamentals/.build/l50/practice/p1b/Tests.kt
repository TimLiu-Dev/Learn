import org.junit.Assert.assertEquals
import org.junit.Test

class LongestWithSumTest {
    @Test fun `a run with negatives inside is found`() {
        assertEquals(4, longestWithSum(intArrayOf(1, -1, 5, -2, 3), 3))
    }

    @Test fun `a run may start at index 0`() {
        assertEquals(2, longestWithSum(intArrayOf(2, 1, 9), 3))
    }

    @Test fun `the earliest start gives the longest run`() {
        assertEquals("[1, -1, 1, -1] has sum 0 over all 4", 4, longestWithSum(intArrayOf(1, -1, 1, -1), 0))
    }

    @Test fun `no run gives 0`() {
        assertEquals(0, longestWithSum(intArrayOf(1, 2, 3), 7))
        assertEquals("empty array", 0, longestWithSum(intArrayOf(), 0))
    }

    @Test fun `sums that pass the Int range still work`() {
        val big = intArrayOf(Int.MAX_VALUE, Int.MAX_VALUE, 1)
        assertEquals(3, longestWithSum(big, 2L * Int.MAX_VALUE + 1))
    }
}
