import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ClockTest {
    @Test fun `twenty minutes after 23 50 is 00 10`() {
        assertEquals(10, minuteAfter(23 * 60 + 50, 20))
    }

    @Test fun `thirty minutes before 00 10 is 23 40`() {
        assertEquals("23:40 is minute 1420", 1420, minuteAfter(10, -30))
    }

    @Test fun `whole days cancel out`() {
        assertEquals(600, minuteAfter(600, -3 * MINUTES_PER_DAY))
        assertEquals(605, minuteAfter(600, 1000 * MINUTES_PER_DAY + 5))
    }

    @Test fun `a huge delta does not overflow`() {
        // Int.MAX_VALUE = 1491308 * 1440 + 127, so this is 1439 + 127 minutes, which wraps to 126
        assertEquals(126, minuteAfter(1439, Int.MAX_VALUE))
        assertEquals(1312, minuteAfter(0, Int.MIN_VALUE))
    }

    @Test fun `halvings is floor of log2`() {
        assertEquals(listOf(0, 1, 1, 3, 9, 29, 62),
            listOf(1L, 2L, 3L, 8L, 1000L, 1_000_000_000L, Long.MAX_VALUE).map { halvings(it) })
    }

    @Test fun `halvings rejects zero`() {
        assertThrows(IllegalArgumentException::class.java) { halvings(0) }
    }
}
