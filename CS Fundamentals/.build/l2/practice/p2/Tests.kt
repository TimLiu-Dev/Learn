import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnixTimeTest {
    @Test fun `leap years skip centuries unless divisible by 400`() {
        assertTrue("2024", isLeap(2024))
        assertFalse("2023", isLeap(2023))
        assertFalse("1900 is a century", isLeap(1900))
        assertTrue("2000 is divisible by 400", isLeap(2000))
    }

    @Test fun `days in a year`() {
        assertEquals(366, daysIn(2024))
        assertEquals(365, daysIn(2023))
    }

    @Test fun `1970 itself is second zero`() {
        assertEquals(0L, secondsSince1970(1970))
    }

    @Test fun `the year 2000 starts at second 946684800`() {
        assertEquals(946_684_800L, secondsSince1970(2000))
    }

    @Test fun `2039 is past Int MAX_VALUE`() {
        assertEquals("2,177,452,800 is more than an Int can hold", 2_177_452_800L, secondsSince1970(2039))
    }
}
