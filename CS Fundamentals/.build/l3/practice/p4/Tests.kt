import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollectionsTest {
    @Test fun `the first word seen twice is returned`() {
        assertEquals("to", firstRepeat(listOf("to", "be", "or", "not", "to", "be")))
    }

    @Test fun `the word whose second copy comes first wins`() {
        assertEquals("b repeats at index 2, before a does at index 3", "b", firstRepeat(listOf("a", "b", "b", "a")))
    }

    @Test fun `no repeat gives null`() {
        assertNull("all different", firstRepeat(listOf("x", "y", "z")))
        assertNull("empty list", firstRepeat(emptyList()))
    }

    @Test fun `low stock comes back in alphabetical order`() {
        val stock = mapOf("pear" to 1, "kiwi" to 9, "apple" to 0, "fig" to 2)
        assertEquals(listOf("apple", "fig", "pear"), lowStock(stock, 3))
    }

    @Test fun `exactly the threshold is not low`() {
        assertEquals("3 is not fewer than 3", emptyList<String>(), lowStock(mapOf("plum" to 3), 3))
        assertEquals("empty stock", emptyList<String>(), lowStock(emptyMap(), 5))
    }
}
