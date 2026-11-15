import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PageTableTest {
    @Test fun `the last byte of page 0 and the first byte of page 1`() {
        assertEquals("page of 16383", 0L, pageNumber(16383))
        assertEquals("offset of 16383", 16383L, offset(16383))
        assertEquals("page of 16384", 1L, pageNumber(16384))
        assertEquals("offset of 16384", 0L, offset(16384))
    }

    @Test fun `the JVM heap address from Part 1 splits cleanly`() {
        val heap = 0x7c0000000L
        assertEquals("page number", 0x1F0000L, pageNumber(heap))
        assertEquals("offset", 0L, offset(heap + PAGE_SIZE * 3))
        assertEquals("offset inside a page", 40L, offset(heap + 40))
    }

    @Test fun `translation keeps the offset and swaps the page`() {
        val table = mapOf(2L to 7L)
        val va = 2 * PAGE_SIZE + 100
        assertEquals(7 * PAGE_SIZE + 100, translate(va, table))
    }

    @Test fun `two processes, same virtual address, different RAM`() {
        val va = 0x7c0000000L + 123
        val processA = mapOf(pageNumber(va) to 5L)
        val processB = mapOf(pageNumber(va) to 9L)
        val a = translate(va, processA)
        val b = translate(va, processB)
        assertEquals(5 * PAGE_SIZE + 123, a)
        assertEquals(9 * PAGE_SIZE + 123, b)
        assertNotEquals("each page table maps to its own frame", a, b)
    }

    @Test fun `an unmapped page is a page fault`() {
        assertNull(translate(3 * PAGE_SIZE, mapOf(2L to 7L)))
    }
}
