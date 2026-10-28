package gateway;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {
    private final AtomicLong now = new AtomicLong(0);
    private final InstantSource clock = () -> Instant.ofEpochMilli(now.get());   // fake clock: we move time by hand
    private final CircuitBreaker breaker = new CircuitBreaker(5, Duration.ofSeconds(1), clock);

    private void fail() { assertThrows(IllegalStateException.class, () -> breaker.call(() -> { throw new IllegalStateException("boom"); })); }

    @Test void opensAfterFiveConsecutiveFailures() {
        for (int i = 0; i < 4; i++) fail();
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state());
        fail();
        assertEquals(CircuitBreaker.State.OPEN, breaker.state());
    }

    @Test void aSuccessResetsTheFailureCount() {
        for (int i = 0; i < 4; i++) fail();
        breaker.call(() -> "ok");
        for (int i = 0; i < 4; i++) fail();
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state());
    }

    @Test void whileOpenItFailsFastWithoutCalling() {
        for (int i = 0; i < 5; i++) fail();
        AtomicBoolean called = new AtomicBoolean(false);
        assertThrows(CircuitOpenException.class, () -> breaker.call(() -> { called.set(true); return "ok"; }));
        assertFalse(called.get());
    }

    @Test void afterTheOpenPeriodOneTrialCallCanCloseIt() {
        for (int i = 0; i < 5; i++) fail();
        now.addAndGet(1000);
        assertEquals(CircuitBreaker.State.HALF_OPEN, breaker.state());
        assertEquals("ok", breaker.call(() -> "ok"));
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state());
    }

    @Test void aFailedTrialCallOpensItAgain() {
        for (int i = 0; i < 5; i++) fail();
        now.addAndGet(1000);
        fail();
        assertEquals(CircuitBreaker.State.OPEN, breaker.state());
        now.addAndGet(999);
        assertEquals(CircuitBreaker.State.OPEN, breaker.state());
    }
}
