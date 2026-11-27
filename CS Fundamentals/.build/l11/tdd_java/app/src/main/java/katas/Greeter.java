package katas;

import java.time.Clock;
import java.time.LocalTime;

public class Greeter {
    private final Clock clock;

    // The caller hands in the clock. Production passes Clock.systemDefaultZone(); a test passes a frozen one.
    public Greeter(Clock clock) {
        this.clock = clock;
    }

    public String greeting() {
        return greetingAt(LocalTime.now(clock));
    }

    // Reads the real clock: the answer depends on when, and where, you run it.
    public static String greetingNow() {
        return greetingAt(LocalTime.now());
    }

    static String greetingAt(LocalTime time) {
        return time.getHour() < 12 ? "Good morning" : "Good afternoon";
    }
}
