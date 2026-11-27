package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Your first red test (L11): it fails until Money.parse works. Make it green,
// then keep going test-first.
class MoneyTest {
    @ParameterizedTest
    @CsvSource({"12, 12.00", "12.5, 12.50", "-12.50, -12.50", "4.35, 4.35"})
    void parsesAmountsExactly(String text, String expected) {
        assertEquals(new Money(new BigDecimal(expected)), Money.parse(text));
    }
}
