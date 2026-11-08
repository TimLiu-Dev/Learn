import org.junit.Assert.assertEquals
import org.junit.Test

class DigitCountTest {
    @Test fun `ordinary numbers`() {
        assertEquals(4, digitCount(1234))
        assertEquals(1, digitCount(7))
    }

    @Test fun `the minus sign is not a digit`() {
        assertEquals(3, digitCount(-907))
    }

    @Test fun `zero has one digit`() {
        assertEquals("0 is written with one digit", 1, digitCount(0))
    }

    @Test fun `the largest Int has ten digits`() {
        assertEquals(10, digitCount(Int.MAX_VALUE))
    }

    @Test fun `the smallest Int has ten digits`() {
        assertEquals("-2147483648 has 10 digits", 10, digitCount(Int.MIN_VALUE))
    }
}
