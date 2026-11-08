import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountingSetTest {
    @Test fun `addAll counts each element once`() {
        val s = CountingSet<String>()
        s.addAll(listOf("a", "b", "c"))
        assertEquals("addCount", 3, s.addCount)
        assertEquals("size", 3, s.size)
    }

    @Test fun `duplicates count as attempts but are stored once`() {
        val s = CountingSet<String>()
        s.add("a")
        assertTrue(s.addAll(listOf("a", "b")))
        assertFalse(s.add("b"))
        assertEquals("addCount", 4, s.addCount)
        assertEquals("size", 2, s.size)
    }

    @Test fun `it is still a full set`() {
        val counting = CountingSet<Int>()
        val asSet: MutableSet<Int> = counting
        asSet.addAll(listOf(1, 2, 3))
        asSet.remove(2)
        assertEquals(setOf(1, 3), asSet.toSet())
        assertEquals("addCount", 3, counting.addCount)
    }

    @Test fun `any set can be wrapped`() {
        val s = CountingSet<String>(LinkedHashSet())
        s.addAll(listOf("c", "a", "b"))
        assertEquals("keeps the inner set's order", listOf("c", "a", "b"), s.toList())
    }
}
