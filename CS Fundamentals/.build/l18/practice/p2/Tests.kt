import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// A read-only list that counts every get(), to prove the search is O(log n).
class CountingList(private val data: IntArray) : AbstractList<Int>() {
    var gets = 0
    override val size get() = data.size
    override fun get(index: Int): Int { gets++; return data[index] }
}

class BinarySearchVariantsTest {
    private val xs = listOf(1, 2, 2, 2, 3, 5, 8)

    @Test fun `finds the first of several duplicates`() {
        assertEquals(1, firstIndexOf(xs, 2))
        assertEquals("first element", 0, firstIndexOf(xs, 1))
        assertEquals("last element", 6, firstIndexOf(xs, 8))
    }

    @Test fun `missing values give -1`() {
        assertEquals("between values", -1, firstIndexOf(xs, 4))
        assertEquals("below everything", -1, firstIndexOf(xs, 0))
        assertEquals("above everything", -1, firstIndexOf(xs, 9))
        assertEquals("empty list", -1, firstIndexOf(emptyList(), 7))
        assertEquals("single element, missing", -1, firstIndexOf(listOf(5), 6))
        assertEquals("single element, present", 0, firstIndexOf(listOf(5), 5))
    }

    @Test fun `a million equal values take about 20 reads, not a million`() {
        val big = CountingList(IntArray(1_000_000) { 7 })
        assertEquals(0, firstIndexOf(big, 7))
        assertTrue("read the list ${big.gets} times", big.gets <= 25)
    }

    @Test fun `insertSorted goes after equal values`() {
        val list = mutableListOf(1, 2, 2, 2, 3)
        assertEquals("returned index", 4, insertSorted(list, 2))
        assertEquals(listOf(1, 2, 2, 2, 2, 3), list)
    }

    @Test fun `insertSorted handles the ends and an empty list`() {
        val list = mutableListOf<Int>()
        assertEquals("into empty", 0, insertSorted(list, 5))
        assertEquals("below everything", 0, insertSorted(list, 1))
        assertEquals("above everything", 2, insertSorted(list, 9))
        assertEquals(listOf(1, 5, 9), list)
    }
}
