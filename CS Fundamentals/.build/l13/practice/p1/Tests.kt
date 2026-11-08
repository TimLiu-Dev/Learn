import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SpanTest {
    @Test fun `both ends are inside the span`() {
        val s = Span(1, 5)
        assertTrue("1 in 1..5", 1 in s)
        assertTrue("3 in 1..5", 3 in s)
        assertTrue("5 in 1..5", 5 in s)
        assertFalse("6 in 1..5", 6 in s)
        assertFalse("0 in 1..5", 0 in s)
    }

    @Test fun `strings compare alphabetically`() {
        val s = Span("b", "d")
        assertTrue("\"cat\" in b..d", "cat" in s)
        assertFalse("\"apple\" in b..d", "apple" in s)
    }

    @Test fun `clamp pulls outsiders to the nearest end`() {
        val s = Span(1, 5)
        assertEquals(1, s.clamp(-3))
        assertEquals(5, s.clamp(9))
        assertEquals(4, s.clamp(4))
    }

    @Test fun `lo greater than hi is rejected, but a single point is fine`() {
        try {
            Span(5, 1)
            fail("Span(5, 1) should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
        assertTrue("Span(3, 3) should allow 3", 3 in Span(3, 3))
    }

    @Test fun `spanOf finds the smallest and largest item`() {
        assertEquals("2..9", spanOf(listOf(4, 9, 2)).toString())
        assertEquals("fig..pear", spanOf(listOf("pear", "fig", "kiwi")).toString())
    }

    @Test fun `spanOf an empty list is null`() {
        assertNull(spanOf(emptyList<Int>()))
    }
}
