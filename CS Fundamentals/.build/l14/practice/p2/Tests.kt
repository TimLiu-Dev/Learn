import org.junit.Assert.assertEquals
import org.junit.Test

class ClosureTest {
    @Test fun `memoize gives the same answers as f`() {
        val square = memoize { it.toLong() * it }
        assertEquals(49L, square(7))
        assertEquals(49L, square(7))
        assertEquals(0L, square(0))
    }

    @Test fun `memoize calls f once per distinct input`() {
        var calls = 0
        val slow = memoize { calls++; it * 10L }
        slow(1); slow(2); slow(1); slow(1); slow(2)
        assertEquals("calls to f", 2, calls)
    }

    @Test fun `two memoized functions keep separate caches`() {
        val double = memoize { it * 2L }
        val triple = memoize { it * 3L }
        assertEquals(10L, double(5))
        assertEquals("triple must not reuse double's cached answer", 15L, triple(5))
    }

    @Test fun `each multiplier keeps its own factor`() {
        assertEquals(listOf(10, 20, 30), multipliers().map { it(10) })
    }
}
