package katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlakyGreeterTest {
    @Test
    void greetsTheMorningShift() {
        assertEquals("Good morning", Greeter.greetingNow());   // written at 9 a.m., so it passed... then
    }
}
