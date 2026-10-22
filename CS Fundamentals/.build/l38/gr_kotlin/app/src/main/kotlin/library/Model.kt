package library

import java.time.LocalDate

data class Member(val id: Long, val name: String)
data class Loan(val id: Long, val bookId: Long, val memberId: Long, val loaned: LocalDate, val returned: LocalDate?)

// The abstractions the POLICY needs (DIP). They live next to LendingService, not next to SQLite.
interface LoanStore {
    fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan
    fun find(loanId: Long): Loan?
    fun openForMember(memberId: Long): List<Loan>
    fun openForBook(bookId: Long): Loan?
    fun close(loanId: Long, returned: LocalDate): Boolean
}

interface MemberStore {
    fun find(memberId: Long): Member?
}
