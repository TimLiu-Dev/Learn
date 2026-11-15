enum class State { CLOSED, OPEN, HALF_OPEN }

// Ask allowRequest() before a call, then report how it went.
// `now` is the clock in ms; tests pass a fake one they can move instantly.
class Breaker(private val threshold: Int, private val openMs: Long, private val now: () -> Long) {
    var state = State.CLOSED
        private set
    private var failures = 0          // failures in a row
    private var openedAt = 0L

    @Synchronized fun allowRequest(): Boolean =
        TODO("CLOSED: yes. OPEN: no, until openMs have passed; then HALF_OPEN and one trial. HALF_OPEN: no")

    @Synchronized fun recordSuccess() {
        TODO("reset the count; a successful trial closes the circuit")
    }

    @Synchronized fun recordFailure() {
        TODO("count it; open at the threshold, or at once if the trial failed")
    }
}
