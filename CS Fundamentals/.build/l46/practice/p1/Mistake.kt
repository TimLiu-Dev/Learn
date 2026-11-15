class FakeClock(var now: Long = 0)

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

    // Only looks at the front of the line.
    private fun expire() {
        while (waiters.isNotEmpty() && clock.now >= waiters.first().deadline) {
            log += "${waiters.removeFirst().who} timed out"
        }
    }

    fun release(conn: String) {
        check(lent.remove(conn)) { "$conn is not lent out" }
        expire()
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
