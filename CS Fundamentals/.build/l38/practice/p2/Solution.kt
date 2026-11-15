import java.time.Clock

data class Hold(val id: String, val bookId: Long, val member: String, val placedAtMillis: Long)

const val HOLD_MILLIS = 3 * 24 * 60 * 60 * 1000L     // a hold lasts 3 days

class HoldShelf(private val clock: Clock, private val newId: () -> String) {
    private val holds = mutableListOf<Hold>()

    fun place(bookId: Long, member: String): Hold =
        Hold(newId(), bookId, member, clock.millis()).also { holds += it }

    // Ask the clock on every call: the time is a dependency, not a constant.
    fun waiting(): List<Hold> = holds.filter { clock.millis() - it.placedAtMillis < HOLD_MILLIS }

    fun pickUp(id: String): Boolean {
        val hold = waiting().find { it.id == id } ?: return false
        return holds.remove(hold)
    }
}
