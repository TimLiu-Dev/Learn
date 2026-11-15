import java.util.Objects;
import java.util.function.LongSupplier;

public class Practice {
    // A leader lease: "I may act as leader for the next durationMs milliseconds."
    // wallMillis works like System.currentTimeMillis(): NTP may step it, even backwards.
    // monoNanos works like System.nanoTime(): it only moves forward, counting nanoseconds.
    static class Lease {
        private final long durationMs;
        private final LongSupplier wallMillis;
        private final LongSupplier monoNanos;
        private final long startedNanos;

        Lease(long durationMs, LongSupplier wallMillis, LongSupplier monoNanos) {
            this.durationMs = durationMs;
            this.wallMillis = wallMillis;
            this.monoNanos = monoNanos;
            this.startedNanos = monoNanos.getAsLong();   // a duration starts on the monotonic clock
        }

        // Milliseconds left on the lease (zero or less once it has run out).
        long remainingMs() { return durationMs - (monoNanos.getAsLong() - startedNanos) / 1_000_000; }

        boolean isValid() { return remainingMs() > 0; }

        // The time of day comes from the wall clock; the amount left from the monotonic one.
        long expiresAtWall() { return wallMillis.getAsLong() + remainingMs(); }
    }

    // CHECKS
    // Fake clocks: real time passes with advance(); NTP steps only the wall clock.
    static class FakeClocks {
        long wall = 1_700_000_000_000L;   // a time of day, in ms
        long mono = 42_000_000_000L;      // an arbitrary starting point, in ns
        void advance(long ms) { wall += ms; mono += ms * 1_000_000; }
        void ntpStep(long ms) { wall += ms; }
        Lease lease(long durationMs) { return new Lease(durationMs, () -> wall, () -> mono); }
    }

    public static void main(String[] args) {
        check("a lease is valid until its time runs out", "true false", () -> {
            FakeClocks c = new FakeClocks(); Lease lease = c.lease(10_000);
            c.advance(9_999); boolean before = lease.isValid();
            c.advance(1); return before + " " + lease.isValid();
        });
        check("remaining time is in milliseconds", 7_500L, () -> {
            FakeClocks c = new FakeClocks(); Lease lease = c.lease(10_000);
            c.advance(2_500); return lease.remainingMs();
        });
        check("stepping the clock back does not stretch the lease", false, () -> {
            FakeClocks c = new FakeClocks(); Lease lease = c.lease(10_000);
            c.advance(1_000); c.ntpStep(-2_000); c.advance(9_000); return lease.isValid();
        });
        check("stepping the clock forward does not cut the lease short", 9_000L, () -> {
            FakeClocks c = new FakeClocks(); Lease lease = c.lease(10_000);
            c.advance(1_000); c.ntpStep(3_600_000); return lease.remainingMs();
        });
        check("the log line shows the time of day it runs out", 1_700_000_008_000L, () -> {
            FakeClocks c = new FakeClocks(); Lease lease = c.lease(10_000);
            c.advance(4_000); c.ntpStep(-2_000); return lease.expiresAtWall();
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
