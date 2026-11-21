import java.time.LocalDate
import java.util.TreeMap

data class Loan(val id: Long, val bookId: Long, val memberId: Long, val loaned: LocalDate, val returned: LocalDate?) {
    val isOpen: Boolean get() = returned == null
}

interface LoanStore {
    fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan
    fun find(id: Long): Loan?
    fun openForBook(bookId: Long): Loan?
    fun forMember(memberId: Long): List<Loan>                   // oldest first
    fun anyForBook(bookId: Long): Boolean
    fun close(id: Long, returned: LocalDate): Boolean           // false: missing or already closed
}

class MemoryLoanStore : LoanStore {
    private val loans = TreeMap<Long, Loan>()      // sorted by id, like ORDER BY id
    private var nextId = 1L

    override fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan {
        // The same promise as SQLite's partial unique index: one open loan per book.
        check(openForBook(bookId) == null) { "book $bookId already has an open loan" }
        return Loan(nextId++, bookId, memberId, loaned, null).also { loans[it.id] = it }
    }

    override fun find(id: Long): Loan? = loans[id]
    override fun openForBook(bookId: Long): Loan? = loans.values.firstOrNull { it.bookId == bookId && it.isOpen }
    override fun forMember(memberId: Long): List<Loan> = loans.values.filter { it.memberId == memberId }
    override fun anyForBook(bookId: Long): Boolean = loans.values.any { it.bookId == bookId }

    override fun close(id: Long, returned: LocalDate): Boolean {
        val loan = loans[id]?.takeIf { it.isOpen } ?: return false
        loans[id] = loan.copy(returned = returned)
        return true
    }
}
