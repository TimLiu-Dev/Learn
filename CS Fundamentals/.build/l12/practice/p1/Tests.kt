import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// A plain message: the page's test view hides text written inside <…>, which assertEquals uses.
fun assertText(expected: String, actual: String?, what: String = "") =
    assertTrue("$what expected \"$expected\" but was \"$actual\"".trim(), expected == actual)

class MedianTest {
    @Test fun `median of an odd count is the middle value`() {
        assertEquals(75, median(mutableListOf(80, 72, 75)))
    }

    @Test fun `median of an even count is the lower middle`() {
        assertEquals(75, median(mutableListOf(79, 72, 80, 75)))
    }

    @Test fun `summary reports first and last in the order taken`() {
        assertText("first=80 last=75 median=75", summary(mutableListOf(80, 72, 75)))
    }

    @Test fun `the caller's list is unchanged afterwards`() {
        val readings = mutableListOf(80, 72, 75)
        summary(readings)
        assertEquals("summary must not reorder the caller's list", listOf(80, 72, 75), readings)
    }

    @Test fun `a single reading is first, last and median`() {
        assertText("first=70 last=70 median=70", summary(mutableListOf(70)))
    }
}
