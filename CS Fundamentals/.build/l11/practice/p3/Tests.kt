import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class StringCalculatorTest {
    @Test fun `step 1 - the empty string is 0`() {
        assertEquals(0, add(""))
    }

    @Test fun `step 2 - one number is itself`() {
        assertEquals(4, add("4"))
        assertEquals(17, add("17"))
    }

    @Test fun `step 3 - two numbers are added`() {
        assertEquals(3, add("1,2"))
    }

    @Test fun `step 4 - any count of numbers is added`() {
        assertEquals(10, add("1,2,3,4"))
        assertEquals(150, add("10,20,30,40,50"))
    }

    @Test fun `step 5 - a newline also separates numbers`() {
        assertEquals(6, add("1\n2,3"))
    }

    @Test fun `step 6 - negatives are rejected, and the message lists them all`() {
        try {
            add("1,-2,3,-4")
            fail("expected an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("negatives not allowed: -2, -4", e.message)
        }
    }
}
