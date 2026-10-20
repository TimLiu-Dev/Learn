package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RomanTest {
    @Test
    fun `1 is I`() {
        assertEquals("I", toRoman(1))
    }

    @Test
    fun `2 is II`() {
        assertEquals("II", toRoman(2))
    }

    @Test
    fun `3 is III`() {
        assertEquals("III", toRoman(3))
    }

    @Test
    fun `4 is IV`() {
        assertEquals("IV", toRoman(4))
    }

    @Test
    fun `5 is V`() {
        assertEquals("V", toRoman(5))
    }

    @Test
    fun `9 is IX`() {
        assertEquals("IX", toRoman(9))
    }

    @Test
    fun `1994 is MCMXCIV`() {
        assertEquals("MCMXCIV", toRoman(1994))
    }

    @Test
    fun `0 is rejected`() {
        assertFailsWith<IllegalArgumentException> { toRoman(0) }
    }
}
