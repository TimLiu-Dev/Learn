import org.junit.Assert.assertEquals
import org.junit.Test

class MedianTest {
    @Test fun `median of an odd count is the middle value`() {
        assertEquals(75, median(mutableListOf(80, 72, 75)))
    }

    @Test fun `median of an even count is the lower middle`() {
        assertEquals(75, median(mutableListOf(79, 72, 80, 75)))
    }

    @Test fun `summary reports first and last in the order taken`() {
        assertEquals("first=80 last=75 median=75", summary(mutableListOf(80, 72, 75)))
    }

    @Test fun `the caller's list is unchanged afterwards`() {
        val readings = mutableListOf(80, 72, 75)
        summary(readings)
        assertEquals("summary must not reorder the caller's list", listOf(80, 72, 75), readings)
    }

    @Test fun `a single reading is first, last and median`() {
        assertEquals("first=70 last=70 median=70", summary(mutableListOf(70)))
    }
}
