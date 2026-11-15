enum class State { CLOSED, OPEN, HALF_OPEN }

// Ask allowRequest() before a call, then report how it went.
// `now` is the clock in ms; tests pass a fake one they can move instantly.
class Breaker(private val threshold: Int, private val openMs: Long, private val now: () -> Long) {
    var state = State.CLOSED
        private set
    private var failures = 0          // failures in a row
    private var openedAt = 0L

    @Synchronized fun allowRequest(): Boolean = when (state) {
        State.CLOSED -> true
        State.OPEN ->
            if (now() - openedAt >= openMs) {
                state = State.HALF_OPEN          // this caller is the one trial
                true
            } else false
        State.HALF_OPEN -> true                  // "it might be fixed now, let them all try"
    }

    @Synchronized fun recordSuccess() {
        failures = 0
        state = State.CLOSED
    }

    @Synchronized fun recordFailure() {
        failures++
        if (state == State.HALF_OPEN || failures >= threshold) {
            state = State.OPEN
            openedAt = now()                     // every (re)open starts a fresh timer
        }
    }
}
