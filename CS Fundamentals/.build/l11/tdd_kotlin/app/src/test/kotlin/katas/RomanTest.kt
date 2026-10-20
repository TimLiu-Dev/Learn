package katas

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RomanTest {
    @ParameterizedTest
    @CsvSource(
        "1, I", "2, II", "3, III", "4, IV", "5, V",
        "9, IX", "40, XL", "1994, MCMXCIV", "3999, MMMCMXCIX",
    )
    fun `converts to roman numerals`(number: Int, expected: String) {
        assertEquals(expected, toRoman(number))
    }

    @ParameterizedTest
    @ValueSource(ints = [0, -1, 4000])
    fun `rejects numbers out of range`(number: Int) {
        assertFailsWith<IllegalArgumentException> { toRoman(number) }
    }
}
