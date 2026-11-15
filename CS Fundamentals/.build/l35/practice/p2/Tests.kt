import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SortedIndexTest {
    // keys 10, 20, 20, 20, 30 (rowids 5, 1, 2, 9, 3), given in a shuffled order
    private fun small() = SortedIndex(listOf(Entry(20, 9), Entry(30, 3), Entry(10, 5), Entry(20, 1), Entry(20, 2)))

    // 200,000 loans, 20,000 members, 10 loans each: like idx_loans_member on big.db
    private fun big() = SortedIndex((1..200_000).map { Entry(it % 20_000 + 1, it) })

    @Test fun `lowerBound finds the first of several equal keys`() {
        assertEquals("lowerBound(20)", 1, small().lowerBound(20))
    }

    @Test fun `lowerBound handles missing keys and both ends`() {
        val idx = small()
        assertEquals("lowerBound(25): 30 is the first key >= 25", 4, idx.lowerBound(25))
        assertEquals("lowerBound(5): every key is bigger", 0, idx.lowerBound(5))
        assertEquals("lowerBound(99): every key is smaller", 5, idx.lowerBound(99))
    }

    @Test fun `lookup returns every rowid for a key, in rowid order`() {
        val idx = small()
        assertEquals(listOf(1, 2, 9), idx.lookup(20))
        assertEquals("a key that isn't there", emptyList<Int>(), idx.lookup(25))
    }

    @Test fun `range walks across keys and stops after hi`() {
        val idx = small()
        assertEquals("range(15, 30)", listOf(1, 2, 9, 3), idx.range(15, 30))
        assertEquals("range(0, 10)", listOf(5), idx.range(0, 10))
        assertEquals("range(31, 40)", emptyList<Int>(), idx.range(31, 40))
    }

    @Test fun `a lookup in 200,000 entries reads a few dozen, not all of them`() {
        val idx = big()
        val rows = idx.lookup(42)
        assertEquals("member 42 has 10 loans", 10, rows.size)
        assertTrue("lookup read ${idx.reads} of ${idx.size} entries: that's a scan, not a search", idx.reads < 40)
    }

    @Test fun `a narrow range is cheap too`() {
        val idx = big()
        val rows = idx.range(19_999, 20_000)
        assertEquals("members 19,999 and 20,000", 20, rows.size)
        assertTrue("range read ${idx.reads} entries; want under 50", idx.reads < 50)
    }
}
