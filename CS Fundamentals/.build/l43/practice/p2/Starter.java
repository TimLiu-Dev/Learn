import java.util.Objects;
import java.util.function.LongSupplier;

public class Practice {
    enum State { CLOSED, OPEN, HALF_OPEN }

    // Ask allowRequest() before a call, then report how it went.
    // `now` is the clock in ms; the checks pass a fake one they can move instantly.
    static class Breaker {
        private final int threshold;
        private final long openMs;
        private final LongSupplier now;
        private State state = State.CLOSED;
        private int failures = 0;         // failures in a row
        private long openedAt = 0;

        Breaker(int threshold, long openMs, LongSupplier now) {
            this.threshold = threshold;
            this.openMs = openMs;
            this.now = now;
        }

        synchronized State state() { return state; }

        synchronized boolean allowRequest() {
            throw new UnsupportedOperationException("TODO: CLOSED yes; OPEN no until openMs passed, then HALF_OPEN and one trial; HALF_OPEN no");
        }

        synchronized void recordSuccess() {
            throw new UnsupportedOperationException("TODO: reset the count; a successful trial closes the circuit");
        }

        synchronized void recordFailure() {
            throw new UnsupportedOperationException("TODO: count it; open at the threshold, or at once if the trial failed");
        }
    }

    // CHECKS
    static long t;

    static Breaker fresh() {
        t = 0;
        return new Breaker(3, 1000, () -> t);
    }

    static void fail(Breaker b, int times) {
        for (int i = 0; i < times; i++) {
            if (!b.allowRequest()) throw new IllegalStateException("a closed circuit must let calls through");
            b.recordFailure();
        }
    }

    public static void main(String[] args) {
        check("three failures in a row open the circuit", "CLOSED OPEN allowed=false", () -> {
            Breaker b = fresh();
            fail(b, 2);
            State afterTwo = b.state();
            fail(b, 1);
            return afterTwo + " " + b.state() + " allowed=" + b.allowRequest();
        });
        check("a success resets the count", State.CLOSED, () -> {
            Breaker b = fresh();
            fail(b, 2);
            b.allowRequest();
            b.recordSuccess();
            fail(b, 2);
            return b.state();
        });
        check("it stays open until openMs have passed", "false true HALF_OPEN", () -> {
            Breaker b = fresh();
            t = 5000;
            fail(b, 3);
            t = 5999;
            boolean early = b.allowRequest();
            t = 6000;
            return early + " " + b.allowRequest() + " " + b.state();
        });
        check("half-open lets exactly one trial through", "true false", () -> {
            Breaker b = fresh();
            fail(b, 3);
            t = 1000;
            return b.allowRequest() + " " + b.allowRequest();
        });
        check("a successful trial closes the circuit", "CLOSED CLOSED", () -> {
            Breaker b = fresh();
            fail(b, 3);
            t = 1000;
            b.allowRequest();
            b.recordSuccess();
            State closed = b.state();
            fail(b, 2);
            return closed + " " + b.state();
        });
        check("a failed trial reopens with a fresh timer", "OPEN false true", () -> {
            Breaker b = fresh();
            fail(b, 3);
            t = 1000;
            b.allowRequest();
            b.recordFailure();
            State reopened = b.state();
            t = 1999;
            boolean early = b.allowRequest();
            t = 2000;
            return reopened + " " + early + " " + b.allowRequest();
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
