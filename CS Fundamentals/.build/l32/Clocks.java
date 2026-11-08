import java.time.*;

public class Clocks {
    // A clock you can set, the way NTP or a user can set the real one
    static class SettableClock extends Clock {
        Instant now;
        SettableClock(Instant now) { this.now = now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    // Inject the clock instead of calling Instant.now() inside the logic (L38 builds on this)
    record Loans(Clock clock) {
        boolean isOverdue(Instant due) { return Instant.now(clock).isAfter(due); }
    }

    public static void main(String[] args) throws InterruptedException {
        Instant due = Instant.parse("2026-11-03T00:00:00Z");
        Clock fixed = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZoneOffset.UTC);
        System.out.println("overdue on Nov 5?   " + new Loans(fixed).isOverdue(due));
        System.out.println("overdue on Nov 2?   " + new Loans(Clock.offset(fixed, Duration.ofDays(-3))).isOverdue(due));

        // Wall clock: "what time is it?" Fine for timestamps, wrong for measuring durations
        var wall = new SettableClock(Instant.parse("2026-11-05T12:00:00Z"));
        long start = wall.millis();
        wall.now = wall.now.plusMillis(250);           // 250 ms of work...
        wall.now = wall.now.minusSeconds(2);           // ...then NTP steps the clock back 2 s
        System.out.println("wall-clock elapsed  " + (wall.millis() - start) + " ms");

        // Monotonic clock: only moves forward, so use it for elapsed time and timeouts
        long t0 = System.nanoTime();
        Thread.sleep(250);
        System.out.println("monotonic elapsed   " + (System.nanoTime() - t0) / 1_000_000 + " ms");
    }
}
