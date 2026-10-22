package desk

sealed interface LendResult {
    data object Lent : LendResult
    data object AlreadyOnLoan : LendResult
    data object NoSuchBook : LendResult
    data object NoSuchMember : LendResult
}

/**
 * A library loan desk. Callers say *what* they want; the order of checks,
 * the id scheme and the storage are hidden in here.
 */
class LoanDesk {
    private val books = mutableMapOf<Long, String>()
    private val members = mutableMapOf<Long, String>()
    private val openLoans = mutableMapOf<Long, Long>()   // bookId -> memberId
    private var nextId = 1L

    fun addBook(title: String): Long = nextId++.also { books[it] = title }

    fun addMember(name: String): Long = nextId++.also { members[it] = name }

    fun lend(bookId: Long, memberId: Long): LendResult = when {
        bookId !in books -> LendResult.NoSuchBook
        memberId !in members -> LendResult.NoSuchMember
        bookId in openLoans -> LendResult.AlreadyOnLoan
        else -> { openLoans[bookId] = memberId; LendResult.Lent }
    }

    /** Takes a book back. Returning a book that is not on loan does nothing: there is no error case. */
    fun giveBack(bookId: Long) {
        openLoans.remove(bookId)
    }

    /** Name of the member who has [bookId], or null if it is on the shelf (or unknown). */
    fun borrowerOf(bookId: Long): String? = openLoans[bookId]?.let { members[it] }
}
