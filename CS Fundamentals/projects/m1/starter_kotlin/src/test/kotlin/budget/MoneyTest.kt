package budget

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

// Your first red test (L11): it fails until Money.parse works. Make it green,
// then keep going test-first.
class MoneyTest {
    @ParameterizedTest
    @CsvSource("12, 1200", "12.5, 1250", "-12.50, -1250", "4.35, 435")
    fun `parses amounts into exact pence`(text: String, cents: Long) {
        kotlin.test.assertEquals(Money(cents), Money.parse(text))
    }
}
