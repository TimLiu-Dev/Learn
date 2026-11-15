data class Reservation(val id: Long, val bookId: Long, val member: String)

// The policy owns this interface (DIP). Every implementation must keep these promises.
interface ReservationStore {
    /** Adds to the back of this book's queue. Ids are 1, 2, 3… across all books, never reused. */
    fun add(bookId: Long, member: String): Reservation

    /** This book's reservations, oldest first. A snapshot: later changes don't show up in it. */
    fun queue(bookId: Long): List<Reservation>

    /** Removes the reservation. False if there's no such id, or it was already cancelled. */
    fun cancel(id: Long): Boolean
}

// The policy. It's finished: don't change it.
class HoldDesk(private val store: ReservationStore) {
    fun reserve(bookId: Long, member: String): Reservation? =
        if (store.queue(bookId).any { it.member == member }) null else store.add(bookId, member)

    fun nextInLine(bookId: Long): String? = store.queue(bookId).firstOrNull()?.member
}

class FakeReservationStore : ReservationStore {
    private val all = mutableListOf<Reservation>()

    // Looks fine until something is cancelled: then an id comes round again.
    override fun add(bookId: Long, member: String) =
        Reservation(all.size + 1L, bookId, member).also { all += it }

    override fun queue(bookId: Long) = all.filter { it.bookId == bookId }

    override fun cancel(id: Long) = all.removeIf { it.id == id }
}
