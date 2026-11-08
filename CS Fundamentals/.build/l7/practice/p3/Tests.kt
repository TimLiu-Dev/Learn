import org.junit.Assert.assertEquals
import org.junit.Test

class AgeInputTest {
    @Test fun `blank text is Blank`() {
        assertEquals(Blank, parseAge(""))
        assertEquals("only spaces", Blank, parseAge("   "))
    }

    @Test fun `a whole number is Valid, after trimming`() {
        assertEquals(Valid(42), parseAge(" 42 "))
        assertEquals("lowest age", Valid(0), parseAge("0"))
        assertEquals("highest age", Valid(150), parseAge("150"))
    }

    @Test fun `words are NotANumber, keeping the trimmed text`() {
        assertEquals(NotANumber("forty"), parseAge(" forty "))
        assertEquals("not a whole number", NotANumber("4.5"), parseAge("4.5"))
    }

    @Test fun `numbers outside 0 to 150 are OutOfRange`() {
        assertEquals(OutOfRange(200), parseAge("200"))
        assertEquals(OutOfRange(-1), parseAge("-1"))
    }

    @Test fun `every case gets its own message`() {
        assertEquals("age 7", describe(Valid(7)))
        assertEquals("not a number: x", describe(NotANumber("x")))
        assertEquals("out of range: 200", describe(OutOfRange(200)))
        assertEquals("please enter your age", describe(Blank))
    }

    @Test fun `validAges keeps only the valid ones, in order`() {
        assertEquals(listOf(30, 7), validAges(listOf("30", "", "abc", "999", " 7")))
    }
}
