package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {
    @ParameterizedTest
    @CsvSource({"12, 12.00", "12.5, 12.50", "-12.50, -12.50", "4.35, 4.35", "0.29, 0.29", "-0.07, -0.07"})
    void parsesExactlyWithTwoDecimals(String text, String printed) {
        assertEquals(printed, Money.parse(text).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12.", ".5", "1.234", "+3", "1,000.00", "12 .00", "£3"})
    void rejectsAnythingThatIsNotPlainPoundsAndPence(String text) {
        assertNull(Money.parse(text));
    }

    @Test
    void sumsExactlyWhereDoubleWouldDrift() {
        assertEquals(Money.parse("0.30"), Money.parse("0.10").plus(Money.parse("0.20")));
        assertEquals(0.30000000000000004, 0.1 + 0.2); // why Money exists
    }

    @Test
    void equalityIgnoresHowTheAmountWasWritten() {
        assertEquals(Money.parse("12.5"), Money.parse("12.50")); // the compact constructor fixes the scale
    }

    @Test
    void percentRoundsHalfEvenToOneDecimal() {
        assertEquals(new BigDecimal("62.2"), Money.parse("249").percentOf(Money.parse("400"))); // 62.25
        assertEquals(new BigDecimal("62.8"), Money.parse("251").percentOf(Money.parse("400"))); // 62.75
    }
}
