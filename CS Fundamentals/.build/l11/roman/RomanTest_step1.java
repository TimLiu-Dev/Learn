package katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RomanTest {
    @Test
    void oneIsI() {
        assertEquals("I", Roman.toRoman(1));
    }
}
