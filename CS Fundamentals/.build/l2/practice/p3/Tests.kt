import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberTrapsTest {
    @Test fun `weekday counts forward`() {
        assertEquals("Mon", weekday(0))
        assertEquals("Tue", weekday(8))
    }

    @Test fun `weekday counts backward`() {
        assertEquals("one day before a Monday", "Sun", weekday(-1))
        assertEquals("nine days before a Monday", "Sat", weekday(-9))
    }

    @Test fun `average keeps the half`() {
        assertEquals(3.5, average(3, 4), 0.0)
        assertEquals(-3.5, average(-3, -4), 0.0)
    }

    @Test fun `average of two huge Ints does not overflow`() {
        assertEquals(2147483647.0, average(Int.MAX_VALUE, Int.MAX_VALUE), 0.0)
    }

    @Test fun `nearly equal doubles count as equal`() {
        assertTrue("0.1 + 0.2 vs 0.3", closeEnough(0.1 + 0.2, 0.3))
    }

    @Test fun `different doubles do not`() {
        assertFalse(closeEnough(0.1, 0.2))
        assertFalse("a millionth apart is too far", closeEnough(1.0, 1.000001))
    }
}
