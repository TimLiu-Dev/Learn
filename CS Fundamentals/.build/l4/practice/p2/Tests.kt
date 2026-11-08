import org.junit.Assert.assertEquals
import org.junit.Test

class CopyTest {
    private fun counts(cs: List<Counter>) = cs.map { it.count }

    @Test fun `the copy has the same counts`() {
        assertEquals(listOf(3, 7), counts(copyCounters(listOf(Counter(3), Counter(7)))))
    }

    @Test fun `adding to the original list doesn't change the copy`() {
        val originals = mutableListOf(Counter(3), Counter(7))
        val copied = copyCounters(originals)
        originals.add(Counter(9))
        assertEquals("copy size", 2, copied.size)
    }

    @Test fun `changing an original counter doesn't change the copy`() {
        val originals = listOf(Counter(3), Counter(7))
        val copied = copyCounters(originals)
        originals[0].count = 99
        assertEquals("the copy still holds the old counts", listOf(3, 7), counts(copied))
    }

    @Test fun `changing the copy doesn't change the originals`() {
        val originals = listOf(Counter(3), Counter(7))
        copyCounters(originals)[1].count = 0
        assertEquals("the originals are untouched", listOf(3, 7), counts(originals))
    }

    @Test fun `an empty list gives an empty copy`() {
        assertEquals(emptyList<Counter>(), copyCounters(emptyList()))
    }
}
