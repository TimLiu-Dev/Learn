class FakeClock(var now: Long = 0)

// A pool of named connections. A borrower who finds none free joins a line, but only
// waits until its deadline; then it gives up (HikariCP calls this connectionTimeout).
class Pool(size: Int, private val clock: FakeClock) {
    private class Waiter(val who: String, val deadline: Long)

    private val free = ArrayDeque((1..size).map { "c$it" })
    private val lent = mutableSetOf<String>()
    private val waiters = ArrayDeque<Waiter>()
    val log = mutableListOf<String>()

    fun borrow(who: String, timeoutMs: Long) {
        val conn = free.removeFirstOrNull()
        if (conn != null) {
            lent += conn
            log += "$who got $conn"
        } else {
            waiters.addLast(Waiter(who, clock.now + timeoutMs))
            log += "$who waits"
        }
    }

    // Deadlines aren't in line order (each borrower picks its own timeout), so check every waiter.
    private fun expire() {
        val expired = waiters.filter { clock.now >= it.deadline }
        waiters.removeAll(expired)
        expired.forEach { log += "${it.who} timed out" }
    }

    fun release(conn: String) {
        check(lent.remove(conn)) { "$conn is not lent out" }
        expire()                                   // the clock may have moved since anyone looked
        val next = waiters.removeFirstOrNull()
        if (next == null) {
            free.addLast(conn)
        } else {
            lent += conn
            log += "${next.who} got $conn"
        }
    }

    fun advance(ms: Long) {
        clock.now += ms
        expire()
    }
}
