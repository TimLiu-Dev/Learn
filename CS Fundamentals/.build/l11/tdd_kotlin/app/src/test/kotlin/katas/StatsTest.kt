package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StatsTest {
    @Test
    fun `average of thirds needs a tolerance`() {
        assertEquals(1.6667, average(1, 2, 2), absoluteTolerance = 0.0001)
    }

    @Test
    fun `average of nothing is zero`() {
        assertEquals(0.0, average())
    }

    @Test
    fun `average of huge values does not overflow`() {
        assertEquals(Int.MAX_VALUE.toDouble(), average(Int.MAX_VALUE, Int.MAX_VALUE))
    }

    @Test
    fun `leap-year rules`() {
        assertTrue(isLeap(2000))
        assertFalse(isLeap(1900))
    }

    @Test
    fun `missing key gives null`() {
        assertNull(mapOf("a" to 1)["b"])
    }
}
