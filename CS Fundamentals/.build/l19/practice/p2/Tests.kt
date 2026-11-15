import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntSetTest {
    @Test fun `adding past half full doubles the capacity`() {
        val s = IntSet()
        for (x in 1..4) s.add(x)
        assertEquals("4 of 8 slots used", 8, s.capacity)
        s.add(5)
        assertEquals("5 of 8 is more than half", 16, s.capacity)
    }

    @Test fun `keys that probed away are found at their new home`() {
        val s = IntSet()
        for (x in listOf(3, 11, 19, 1, 2)) s.add(x)   // 3, 11, 19 all start at slot 3 of 8
        assertEquals(16, s.capacity)
        assertTrue("11 after growing", s.contains(11))
        assertTrue("19 after growing", s.contains(19))
    }

    @Test fun `a hundred elements survive five resizes`() {
        val s = IntSet()
        for (x in 0 until 100) s.add(x * 7)
        assertEquals("size", 100, s.size)
        assertEquals("capacity", 256, s.capacity)
        for (x in 0 until 100) assertTrue("contains ${x * 7}", s.contains(x * 7))
        assertFalse(s.contains(5))
    }

    @Test fun `duplicates are not added twice, even across a resize`() {
        val s = IntSet()
        for (x in 1..5) s.add(x)
        assertFalse("1 is already there", s.add(1))
        assertEquals("size", 5, s.size)
    }

    @Test fun `negative numbers move too`() {
        val s = IntSet()
        for (x in listOf(-1, -9, -17, -25, -33)) s.add(x)
        assertTrue(s.contains(-17))
        assertTrue(s.contains(-33))
    }
}
