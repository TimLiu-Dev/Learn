package katas

import kotlin.test.Test
import kotlin.test.assertEquals

class RomanTest {
    @Test
    fun `1 is I`() {
        assertEquals("I", toRoman(1))
    }
}
