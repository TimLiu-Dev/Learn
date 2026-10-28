import java.time.Duration
import java.time.Instant
import java.time.InstantSource
import java.util.concurrent.atomic.AtomicLong

class CircuitOpenException : RuntimeException("circuit open: failing fast")

// CLOSED: calls go through; count consecutive failures.
// OPEN: fail fast without calling, until `openFor` has passed.
// HALF_OPEN: let one trial call through; success closes, failure re-opens.
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

fun main() {
    val now = AtomicLong(0)                                    // a fake clock we move by hand (L38)
    val clock = InstantSource { Instant.ofEpochMilli(now.get()) }
    val breaker = CircuitBreaker(3, Duration.ofMillis(500), clock) { from, to -> println("        state $from -> $to") }
    var calls = 0
    val dependency = { calls++; if (now.get() in 300 until 1500) error("503 from pricing") else "price 12.99" }

    while (now.get() <= 2400) {
        val outcome = try { "ok    ${breaker.call(dependency)}" }
                      catch (e: CircuitOpenException) { "fast fail (not called)" }
                      catch (e: IllegalStateException) { "error ${e.message}" }
        println("t=%4d  %-26s %s".format(now.get(), outcome, breaker.state()))
        now.addAndGet(100)
    }
    println("dependency was actually called $calls times out of 25")
}
