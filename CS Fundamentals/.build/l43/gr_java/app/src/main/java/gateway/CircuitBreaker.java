package gateway;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** CLOSED → (threshold consecutive failures) → OPEN → (after openFor) → HALF_OPEN → one trial call. */
public final class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final Duration openFor;
    private final InstantSource clock;
    private final BiConsumer<State, State> onChange;
    private State state = State.CLOSED;
    private int failures = 0;
    private Instant openedAt = Instant.EPOCH;
    private boolean trialInFlight = false;

    public CircuitBreaker(int failureThreshold, Duration openFor, InstantSource clock) { this(failureThreshold, openFor, clock, (a, b) -> {}); }

    public CircuitBreaker(int failureThreshold, Duration openFor, InstantSource clock, BiConsumer<State, State> onChange) {
        this.failureThreshold = failureThreshold; this.openFor = openFor; this.clock = clock; this.onChange = onChange;
    }

    public synchronized State state() {
        if (state == State.OPEN && !clock.instant().isBefore(openedAt.plus(openFor))) moveTo(State.HALF_OPEN);
        return state;
    }

    public <T> T call(Supplier<T> block) {
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
