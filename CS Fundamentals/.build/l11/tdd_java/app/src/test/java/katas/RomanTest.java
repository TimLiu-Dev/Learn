package katas;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RomanTest {
    @ParameterizedTest
    @CsvSource({
        "1, I", "2, II", "3, III", "4, IV", "5, V",
        "9, IX", "40, XL", "1994, MCMXCIV", "3999, MMMCMXCIX"
    })
    void convertsToRomanNumerals(int number, String expected) {
        assertEquals(expected, Roman.toRoman(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 4000})
    void rejectsNumbersOutOfRange(int number) {
        assertThrows(IllegalArgumentException.class, () -> Roman.toRoman(number));
    }
}
