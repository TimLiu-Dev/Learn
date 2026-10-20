package katas

import kotlin.test.Test
import kotlin.test.assertEquals

class PriceTest {
    @Test
    fun `formats cents with two digits`() {
        // Arrange
        val cents = 1234L
        // Act
        val result = formatPrice(cents)
        // Assert
        assertEquals("$12.34", result)
    }

    @Test
    fun `pads single-digit cents with a zero`() {
        assertEquals("$12.05", formatPrice(1205))
    }

    @Test
    fun `can hide the cents`() {
        assertEquals("$12", formatPrice(1205, showCents = false))
    }
}
