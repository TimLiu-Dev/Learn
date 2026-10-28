package gateway

import java.time.Duration
import java.time.Instant
import java.time.InstantSource
import java.util.Random
import kotlin.math.ceil
import kotlin.math.min

/** Up to [capacity] tokens, refilled at [perSecond]; each request takes one. */
class TokenBucket(private val capacity: Double, private val perSecond: Double, private val clock: InstantSource) {
    private var tokens = capacity
    private var last = clock.millis()

    @Synchronized fun tryAcquire(): Boolean {
        refill()
        if (tokens < 1) return false
        tokens -= 1
        return true
    }

    /** Whole seconds until a token is free: the value for a Retry-After header. */
    @Synchronized fun retryAfterSeconds(): Long { refill(); return ceil((1 - tokens) / perSecond).toLong().coerceAtLeast(1) }

    private fun refill() {
        val now = clock.millis()
        tokens = min(capacity, tokens + (now - last) / 1000.0 * perSecond)
        last = now
    }
}

class CircuitOpenException : RuntimeException("circuit open")

/** CLOSED → (threshold consecutive failures) → OPEN → (after openFor) → HALF_OPEN → one trial call. */
class CircuitBreaker(
    private val failureThreshold: Int,
    private val openFor: Duration,
    private val clock: InstantSource,
    private val onChange: (State, State) -> Unit = { _, _ -> },
) {
    enum class State { CLOSED, OPEN, HALF_OPEN }

    private var state = State.CLOSED
    private var failures = 0
    private var openedAt: Instant = Instant.EPOCH
    private var trialInFlight = false

    @Synchronized fun state(): State {
        if (state == State.OPEN && !clock.instant().isBefore(openedAt + openFor)) moveTo(State.HALF_OPEN)
        return state
    }

    fun <T> call(block: () -> T): T {
        synchronized(this) {
            when (state()) {
                State.OPEN -> throw CircuitOpenException()
                State.HALF_OPEN -> if (trialInFlight) throw CircuitOpenException() else trialInFlight = true
                State.CLOSED -> {}
            }
        }
        val result = try { block() } catch (e: Exception) { onFailure(); throw e }
        onSuccess()
        return result
    }

    @Synchronized private fun onSuccess() {
        failures = 0; trialInFlight = false
        if (state != State.CLOSED) moveTo(State.CLOSED)
    }

    @Synchronized private fun onFailure() {
        trialInFlight = false
        failures++
        if (state == State.HALF_OPEN || failures >= failureThreshold) { openedAt = clock.instant(); moveTo(State.OPEN) }
    }

    private fun moveTo(next: State) { val old = state; state = next; onChange(old, next) }
}

/** Capped exponential backoff with full jitter. Only [retryable] failures are retried. */
fun <T> retry(maxAttempts: Int, baseMs: Long, capMs: Long, random: Random,
              retryable: (Exception) -> Boolean, block: (attempt: Int) -> T): T {
    var attempt = 1
    while (true) {
        try {
            return block(attempt)
        } catch (e: Exception) {
            if (!retryable(e) || attempt == maxAttempts) throw e
            val backoff = min(capMs, baseMs shl (attempt - 1))
            Thread.sleep((synchronized(random) { random.nextDouble() } * backoff).toLong())
            attempt++
        }
    }
}
