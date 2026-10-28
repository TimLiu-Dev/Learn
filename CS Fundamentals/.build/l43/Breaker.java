import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Breaker {
    public static void main(String[] args) {
        AtomicLong now = new AtomicLong(0);                    // a fake clock we move by hand (L38)
        InstantSource clock = () -> Instant.ofEpochMilli(now.get());
        CircuitBreaker breaker = new CircuitBreaker(3, Duration.ofMillis(500), clock,
                (from, to) -> System.out.println("        state " + from + " -> " + to));
        AtomicInteger calls = new AtomicInteger();
        Supplier<String> dependency = () -> {
            calls.incrementAndGet();
            if (now.get() >= 300 && now.get() < 1500) throw new IllegalStateException("503 from pricing");
            return "price 12.99";
        };
        while (now.get() <= 2400) {
            String outcome;
            try { outcome = "ok    " + breaker.call(dependency); }
            catch (CircuitOpenException e) { outcome = "fast fail (not called)"; }
            catch (IllegalStateException e) { outcome = "error " + e.getMessage(); }
            System.out.printf("t=%4d  %-26s %s%n", now.get(), outcome, breaker.state());
            now.addAndGet(100);
        }
        System.out.println("dependency was actually called " + calls.get() + " times out of 25");
    }
}

class CircuitOpenException extends RuntimeException {
    CircuitOpenException() { super("circuit open: failing fast"); }
}

// CLOSED: calls go through; count consecutive failures.
// OPEN: fail fast without calling, until `openFor` has passed.
// HALF_OPEN: let one trial call through; success closes, failure re-opens.
class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final Duration openFor;
    private final InstantSource clock;
    private final BiConsumer<State, State> onChange;
    private State state = State.CLOSED;
    private int failures = 0;
    private Instant openedAt = Instant.EPOCH;
    private boolean trialInFlight = false;

    CircuitBreaker(int failureThreshold, Duration openFor, InstantSource clock, BiConsumer<State, State> onChange) {
        this.failureThreshold = failureThreshold; this.openFor = openFor; this.clock = clock; this.onChange = onChange;
    }

    synchronized State state() {
        if (state == State.OPEN && !clock.instant().isBefore(openedAt.plus(openFor))) moveTo(State.HALF_OPEN);
        return state;
    }

    <T> T call(Supplier<T> block) {
        synchronized (this) {
            switch (state()) {
                case OPEN -> throw new CircuitOpenException();
                case HALF_OPEN -> { if (trialInFlight) throw new CircuitOpenException(); trialInFlight = true; }
                case CLOSED -> { }
            }
        }
        T result;
        try { result = block.get(); } catch (RuntimeException e) { onFailure(); throw e; }
        onSuccess();
        return result;
    }

    private synchronized void onSuccess() {
        failures = 0; trialInFlight = false;
        if (state != State.CLOSED) moveTo(State.CLOSED);
    }

    private synchronized void onFailure() {
        trialInFlight = false;
        failures++;
        if (state == State.HALF_OPEN || failures >= failureThreshold) { openedAt = clock.instant(); moveTo(State.OPEN); }
    }

    private void moveTo(State next) { State old = state; state = next; onChange.accept(old, next); }
}
