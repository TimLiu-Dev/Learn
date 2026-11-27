class FakeClock(var now: Long = 0)

// A pool of named connections. A borrower who finds none free joins a line, but only
// waits until its deadline; then it gives up (HikariCP calls this connectionTimeout).
class Pool(size: Int, private val clock: FakeClock) {
    private class Waiter(val who: String, val deadline: Long)

    private val free = ArrayDeque((1..size).map { "c$it" })
    private val lent = mutableSetOf<String>()
    private val waiters = ArrayDeque<Waiter>()
    val log = mutableListOf<String>()

    // Logs "<who> got <conn>" if a connection is free. Otherwise logs "<who> waits"
    // and joins the line with deadline = now + timeoutMs.
    fun borrow(who: String, timeoutMs: Long) {
        TODO("hand out a free connection, or join the line")
    }

    // Logs "<who> timed out" for every waiter whose deadline has come (now >= deadline).
    private fun expire() {
        TODO("remove and log every waiter that has run out of time")
    }

    // Gives conn to the longest-waiting borrower who is still waiting, or puts it back.
    // Releasing a connection that isn't lent out is a bug: throw IllegalStateException.
    fun release(conn: String) {
        TODO("check it was lent, drop timed-out waiters, then serve the line or the free list")
    }

    fun advance(ms: Long) {
        clock.now += ms
        expire()
    }
}
