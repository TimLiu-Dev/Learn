import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StallTest {
    private val week = listOf(
        Sale("Mon", "tea", 3), Sale("Mon", "cake", 1),
        Sale("Tue", "tea", 2), Sale("Tue", "jam", 6),
        Sale("Wed", "cake", 4), Sale("Wed", "tea", 4),
    )

    @Test fun `units are totalled per item in first-seen order`() {
        val units = unitsPerItem(week)
        assertEquals(mapOf("tea" to 9, "cake" to 5, "jam" to 6), units)
        assertEquals("key order", listOf("tea", "cake", "jam"), units.keys.toList())
    }

    @Test fun `the top item has the biggest total, not the biggest sale`() {
        assertEquals("tea sells 9 in total; jam's single sale of 6 is the biggest", "tea", topItem(week))
    }

    @Test fun `no sales means no top item`() {
        assertNull(topItem(emptyList()))
    }

    @Test fun `splitBySize keeps both halves in order`() {
        val (big, small) = splitBySize(week, 4)
        assertEquals(listOf("jam", "cake", "tea"), big.map { it.item })
        assertEquals(listOf("tea", "cake", "tea"), small.map { it.item })
    }

    @Test fun `changes compares each total with the next`() {
        assertEquals(listOf(3, -2), changes(listOf(5, 8, 6)))
        assertEquals("one day has nothing to compare", emptyList<Int>(), changes(listOf(7)))
        assertEquals(emptyList<Int>(), changes(emptyList()))
    }
}
