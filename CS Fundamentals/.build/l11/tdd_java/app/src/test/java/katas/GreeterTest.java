package katas;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest {
    private static Clock clockAt(String instant) {
        return Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    }

    @Test
    void oneSecondBeforeNoonIsStillMorning() {
        assertEquals("Good morning", new Greeter(clockAt("2026-03-02T11:59:59Z")).greeting());
    }

    @Test
    void noonIsAfternoon() {
        assertEquals("Good afternoon", new Greeter(clockAt("2026-03-02T12:00:00Z")).greeting());
    }
}
