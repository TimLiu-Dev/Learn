import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PairsTest {
    @Test fun `twoSum finds the classic pair`() {
        assertEquals(0 to 1, twoSum(listOf(2, 7, 11, 15), 9))
        assertEquals(1 to 2, twoSum(listOf(3, 2, 4), 6))
    }

    @Test fun `a number can't pair with itself`() {
        assertNull("3 + 3 needs two 3s", twoSum(listOf(3, 4), 6))
        assertEquals("two equal numbers are fine", 0 to 1, twoSum(listOf(3, 3), 6))
    }

    @Test fun `twoSum handles negatives and no answer`() {
        assertEquals(0 to 2, twoSum(listOf(-3, 4, 3, 90), 0))
        assertNull(twoSum(listOf(1, 2, 3), 100))
        assertNull(twoSum(emptyList(), 0))
    }

    @Test fun `the pair that completes first wins`() {
        assertEquals("1 + 5 completes at index 2, before 2 + 4", 0 to 2, twoSum(listOf(1, 2, 5, 4), 6))
    }

    @Test fun `countPairs counts every index pair`() {
        assertEquals("1+5, 1+5, 7+(-1)", 3, countPairs(listOf(1, 5, 7, -1, 5), 6))
        assertEquals("three 3s make three pairs", 3, countPairs(listOf(3, 3, 3), 6))
        assertEquals(0, countPairs(listOf(3), 6))
        assertEquals(0, countPairs(emptyList(), 6))
    }
}
