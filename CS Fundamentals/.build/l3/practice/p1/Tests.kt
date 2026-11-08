import org.junit.Assert.assertEquals
import org.junit.Test

class FlowTest {
    @Test fun `prices change exactly at the age boundaries`() {
        assertEquals("age 2", 0, ticketPrice(2))
        assertEquals("age 3", 5, ticketPrice(3))
        assertEquals("age 12", 5, ticketPrice(12))
        assertEquals("age 13", 12, ticketPrice(13))
        assertEquals("age 64", 12, ticketPrice(64))
        assertEquals("age 65", 6, ticketPrice(65))
    }

    @Test fun `a newborn is free and a negative age is invalid`() {
        assertEquals("age 0", 0, ticketPrice(0))
        assertEquals("age -1", -1, ticketPrice(-1))
    }

    @Test fun `multiples of 3 and 5 say Fizz and Buzz`() {
        assertEquals("Fizz", fizzBuzz(3))
        assertEquals("Buzz", fizzBuzz(5))
        assertEquals("7", fizzBuzz(7))
    }

    @Test fun `multiples of both say FizzBuzz`() {
        assertEquals("FizzBuzz", fizzBuzz(15))
        assertEquals("FizzBuzz", fizzBuzz(30))
    }

    @Test fun `the line joins 1 to n with single spaces`() {
        assertEquals("1 2 Fizz 4 Buzz", fizzBuzzLine(5))
        assertEquals("1", fizzBuzzLine(1))
        assertEquals("n = 0 gives an empty line", "", fizzBuzzLine(0))
    }
}
