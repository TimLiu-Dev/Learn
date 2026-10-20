package katas;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParamTest {
    @ParameterizedTest
    @ValueSource(ints = {1996, 2000, 2024})
    void leapYears(int year) {
        assertTrue(Stats.isLeap(year));
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 2023, 2100})
    void commonYears(int year) {
        assertFalse(Stats.isLeap(year));
    }

    @ParameterizedTest
    @CsvSource({
        "1234, $12.34",
        "1205, $12.05",
        "99,   $0.99",
        "0,    $0.00"
    })
    void formatsPrices(long cents, String expected) {
        assertEquals(expected, Price.formatPrice(cents));
    }
}
