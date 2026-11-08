import org.junit.Assert.assertEquals
import org.junit.Test

class FractionTest {
    private fun show(f: Fraction) = "${f.num}/${f.den}"

    @Test fun `a fraction is stored in lowest terms`() {
        assertEquals("3/4", show(Fraction(6, 8)))
        assertEquals("0/1", show(Fraction(0, 5)))
    }

    @Test fun `the denominator defaults to 1`() {
        assertEquals("5/1", show(Fraction(5)))
    }

    @Test fun `the sign always lives in the numerator`() {
        assertEquals("-1/2", show(Fraction(1, -2)))
        assertEquals("2/3", show(Fraction(-4, -6)))
    }

    @Test fun `a zero denominator is rejected`() {
        val e = runCatching { Fraction(3, 0) }.exceptionOrNull()
        assertEquals("Fraction(3, 0) should throw IllegalArgumentException, not " + e,
            IllegalArgumentException::class.java, e?.javaClass)
    }

    @Test fun `zero over zero is rejected before any arithmetic`() {
        val e = runCatching { Fraction(0, 0) }.exceptionOrNull()
        assertEquals("Fraction(0, 0) should throw IllegalArgumentException, not " + e,
            IllegalArgumentException::class.java, e?.javaClass)
    }

    @Test fun `the text constructor parses and simplifies`() {
        assertEquals("1/2", show(Fraction("4/8")))
        assertEquals("-3/4", show(Fraction("3/-4")))
    }
}
