package catalogue.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class IsbnTest {
    @Test
    void hyphensDontMatterDigitsDo() {
        assertEquals(Isbn.parse("978-0-441-47812-5"), Isbn.parse("9780441478125"));
    }

    @Test
    void aWrongCheckDigitIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> Isbn.parse("978-0-441-47812-4"));
    }

    @Test
    void twelveDigitsAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Isbn.parse("978044147812"));
    }
}
