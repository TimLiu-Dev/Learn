package budget

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoneyTest {
    @ParameterizedTest
    @CsvSource("12, 1200", "12.5, 1250", "-12.50, -1250", "4.35, 435", "0.29, 29", "-0.07, -7")
    fun `parses exactly, with no float rounding`(text: String, cents: Long) {
        assertEquals(Money(cents), Money.parse(text))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "12.", ".5", "1.234", "+3", "1,000.00", "12 .00", "£3", "99999999999999999999"])
    fun `rejects anything that isn't plain pounds and pence`(text: String) {
        assertNull(Money.parse(text))
    }

    @Test
    fun `formats with two decimals and a leading minus`() {
        assertEquals("-1234.05", Money(-123405).toString())
        assertEquals("0.07", Money(7).toString())
        assertEquals("0.00", Money.ZERO.toString())
    }

    @Test
    fun `sums exactly where Double would drift`() {
        assertEquals(Money(30), listOf(Money(10), Money(20)).sum())
        assertEquals("0.30000000000000004", (0.1 + 0.2).toString()) // why Money exists
    }

    @Test
    fun `percent rounds HALF_EVEN to one decimal`() {
        assertEquals(BigDecimal("62.2"), Money(24900).percentOf(Money(40000))) // 62.25 -> 62.2
        assertEquals(BigDecimal("62.8"), Money(25100).percentOf(Money(40000))) // 62.75 -> 62.8
    }
}
