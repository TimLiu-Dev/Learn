import org.junit.Assert.assertEquals
import org.junit.Test

class SubsetSumTest {
    @Test fun `1 to 5 with target 5 gives three subsets in search order`() {
        assertEquals(listOf(listOf(1, 4), listOf(2, 3), listOf(5)), subsetSums(listOf(1, 2, 3, 4, 5), 5))
    }

    @Test fun `each number is used at most once`() {
        assertEquals("2 + 2 would reuse the 2", emptyList<List<Int>>(), subsetSums(listOf(2, 3), 4))
    }

    @Test fun `no subset reaches the target`() {
        assertEquals(emptyList<List<Int>>(), subsetSums(listOf(5, 6), 4))
    }

    @Test fun `target 0 has one answer, the empty subset`() {
        assertEquals(listOf(emptyList<Int>()), subsetSums(listOf(1, 2), 0))
    }

    @Test fun `every answer is a separate list`() {
        val found = subsetSums(listOf(1, 2, 3, 4, 5, 6), 6)
        assertEquals(listOf(listOf(1, 2, 3), listOf(1, 5), listOf(2, 4), listOf(6)), found)
    }

    @Test(timeout = 2000) fun `pruning makes 2 to the 40 subsets fast`() {
        assertEquals(listOf(listOf(1, 2), listOf(3)), subsetSums((1..40).toList(), 3))
    }
}
