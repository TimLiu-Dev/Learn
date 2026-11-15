import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// A list that counts every element read, by index or by a for loop.
class CountingList(private val items: List<Int>) : AbstractList<Int>() {
    var reads = 0
        private set
    override val size: Int get() = items.size
    override fun get(index: Int): Int {
        reads++
        return items[index]
    }
}

class PairSumTest {
    @Test fun `finds a pair that adds up to the target`() {
        assertTrue(hasPairSum(listOf(3, 8, 5), 13))
    }

    @Test fun `no pair means false`() {
        assertFalse(hasPairSum(listOf(3, 8, 5), 100))
        assertFalse("an empty list has no pairs", hasPairSum(emptyList(), 0))
    }

    @Test fun `one element can't pair with itself`() {
        assertFalse("[5] has no pair adding up to 10", hasPairSum(listOf(5), 10))
    }

    @Test fun `two equal elements can pair`() {
        assertTrue(hasPairSum(listOf(5, 1, 5), 10))
    }

    @Test fun `negative numbers work`() {
        assertTrue(hasPairSum(listOf(-4, 7, 2), 3))
    }

    @Test fun `one pass reads each element a few times, not once per pair`() {
        val n = 2_000
        val xs = CountingList((0 until n).toList())
        assertFalse(hasPairSum(xs, -1))          // no pair: the worst case
        assertTrue("read the list ${xs.reads} times for $n elements: that's not O(n)", xs.reads <= 4 * n)
    }
}
