package katas

import kotlin.test.Test
import kotlin.test.assertEquals

class EqualityDemoTest {
    @Test
    fun `data classes compare by contents`() {
        assertEquals(DataPoint(1, 2), DataPoint(1, 2))
    }

    @Test
    fun `plain classes compare by identity`() {
        assertEquals(PlainPoint(1, 2), PlainPoint(1, 2))
    }
}
