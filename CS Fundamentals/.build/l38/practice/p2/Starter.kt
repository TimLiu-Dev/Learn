import java.time.Clock
import java.util.UUID

data class Hold(val id: String, val bookId: Long, val member: String, val placedAtMillis: Long)

const val HOLD_MILLIS = 3 * 24 * 60 * 60 * 1000L     // a hold lasts 3 days

class HoldShelf(
    private val clock: Clock,            // TODO: read the time from here, not System.currentTimeMillis()
    private val newId: () -> String,     // TODO: take ids from here, not UUID.randomUUID()
) {
    private val holds = mutableListOf<Hold>()

    fun place(bookId: Long, member: String): Hold {
        val hold = Hold(UUID.randomUUID().toString(), bookId, member, System.currentTimeMillis())
        holds += hold
        return hold
    }

    // Holds younger than 3 days. Older ones have expired: the book goes back on the open shelf.
    fun waiting(): List<Hold> = holds.filter { System.currentTimeMillis() - it.placedAtMillis < HOLD_MILLIS }

    // True if the hold is still waiting; it then leaves the shelf. False if it's unknown or expired.
    fun pickUp(id: String): Boolean = TODO("find the hold among the waiting ones, remove it")
}
