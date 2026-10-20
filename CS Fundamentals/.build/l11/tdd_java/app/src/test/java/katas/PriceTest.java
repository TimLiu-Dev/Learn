package katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceTest {
    @Test
    void formatsCentsWithTwoDigits() {
        // Arrange
        long cents = 1234L;
        // Act
        String result = Price.formatPrice(cents);
        // Assert
        assertEquals("$12.34", result);
    }

    @Test
    void padsSingleDigitCentsWithAZero() {
        assertEquals("$12.05", Price.formatPrice(1205));
    }

    @Test
    void canHideTheCents() {
        assertEquals("$12", Price.formatPrice(1205, "$", false));
    }
}
