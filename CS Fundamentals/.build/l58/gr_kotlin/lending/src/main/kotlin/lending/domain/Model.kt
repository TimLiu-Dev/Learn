package lending.domain

import java.time.LocalDate

// Lending's own words. A "copy" here is a physical item on the shelf; lending never needs
// an author or an ISBN, so it doesn't have them. Value objects: compared by value, valid on creation.
data class CopyId(val barcode: String) {
    init { require(barcode.isNotBlank()) { "a barcode can't be blank" } }
}

data class MemberId(val value: Long)

data class Loan(val copy: CopyId, val loaned: LocalDate, val returned: LocalDate? = null) {
    val due: LocalDate get() = loaned.plusDays(Borrower.LOAN_DAYS)
    val isOpen: Boolean get() = returned == null
}

// Lending's idea of a "book": one copy, whether it may go out, and who has it.
data class ShelfCopy(val id: CopyId, val title: String, val withdrawn: Boolean = false, val onLoanTo: MemberId? = null)

sealed interface LendResult {
    data class Lent(val loan: Loan) : LendResult
    data class Blocked(val reason: String) : LendResult
    data object CopyOnLoan : LendResult
    data object NotLendable : LendResult
    data object UnknownCopy : LendResult
}

sealed interface ReturnResult {
    data class Returned(val feeCents: Long) : ReturnResult
    data object NotBorrowed : ReturnResult
}

// Ports: what the lending rules need from the outside world. Adapters implement them.
interface Shelf {
    fun find(id: CopyId): ShelfCopy?
    fun save(copy: ShelfCopy)
}

interface Borrowers {
    fun find(id: MemberId): Borrower           // a member with no history yet gets a fresh Borrower
    fun save(borrower: Borrower)
}
