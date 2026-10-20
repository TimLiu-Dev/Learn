package katas

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParamTest {
    @ParameterizedTest
    @ValueSource(ints = [1996, 2000, 2024])
    fun `leap years`(year: Int) {
        assertTrue(isLeap(year))
    }

    @ParameterizedTest
    @ValueSource(ints = [1900, 2023, 2100])
    fun `common years`(year: Int) {
        assertFalse(isLeap(year))
    }

    @ParameterizedTest
    @CsvSource(
        "1234, $12.34",
        "1205, $12.05",
        "99,   $0.99",
        "0,    $0.00",
    )
    fun `formats prices`(cents: Long, expected: String) {
        assertEquals(expected, formatPrice(cents))
    }
}
