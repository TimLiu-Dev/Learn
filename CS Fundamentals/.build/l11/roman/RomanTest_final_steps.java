package katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RomanTest {
    @Test
    void oneIsI() {
        assertEquals("I", Roman.toRoman(1));
    }

    @Test
    void twoIsII() {
        assertEquals("II", Roman.toRoman(2));
    }

    @Test
    void threeIsIII() {
        assertEquals("III", Roman.toRoman(3));
    }

    @Test
    void fourIsIV() {
        assertEquals("IV", Roman.toRoman(4));
    }

    @Test
    void fiveIsV() {
        assertEquals("V", Roman.toRoman(5));
    }

    @Test
    void nineIsIX() {
        assertEquals("IX", Roman.toRoman(9));
    }

    @Test
    void year1994IsMCMXCIV() {
        assertEquals("MCMXCIV", Roman.toRoman(1994));
    }

    @Test
    void zeroIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Roman.toRoman(0));
    }
}
