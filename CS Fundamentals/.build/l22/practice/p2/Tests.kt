import org.junit.Assert.assertEquals
import org.junit.Test

class CacheMissTest {
    @Test fun `column-major visits down each column first`() {
        assertEquals(listOf(0, 3, 6, 1, 4, 7, 2, 5, 8), columnMajor(3))
    }

    @Test fun `row-major on the stepper's grid misses once per line`() {
        assertEquals("8x8 grid, 4 ints per line, 4 lines", 16, misses(rowMajor(8), 4, 4))
    }

    @Test fun `column-major on the stepper's grid misses every time`() {
        assertEquals("8x8 grid, 4 ints per line, 4 lines", 64, misses(columnMajor(8), 4, 4))
    }

    @Test fun `a cache big enough for the whole grid rescues column-major`() {
        assertEquals("16 lines hold all 64 ints", 16, misses(columnMajor(8), 4, 16))
    }

    @Test fun `a hit makes a line recently used`() {
        // lines 0, 1, 0, 2, 0 with room for 2: LRU evicts line 1 for line 2, so the last 0 hits
        assertEquals("LRU, not first-in-first-out", 3, misses(listOf(0, 4, 0, 8, 0), 4, 2))
    }

    @Test fun `no accesses, no misses`() {
        assertEquals(0, misses(emptyList(), 4, 4))
    }
}
