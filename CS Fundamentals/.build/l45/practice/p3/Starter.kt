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

    override fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan =
        TODO("refuse a second open loan for the book, then store a new Loan")
    override fun find(id: Long): Loan? = TODO()
    override fun openForBook(bookId: Long): Loan? = TODO()
    override fun forMember(memberId: Long): List<Loan> = TODO()
    override fun anyForBook(bookId: Long): Boolean = TODO()
    override fun close(id: Long, returned: LocalDate): Boolean = TODO("only an open loan can be closed")
}
