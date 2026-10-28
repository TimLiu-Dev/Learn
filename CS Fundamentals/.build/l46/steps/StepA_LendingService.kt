package shelf.service

import java.time.Clock
import java.time.LocalDate
import shelf.domain.Loan
import shelf.domain.Member
import shelf.store.BookAlreadyOnLoan
import shelf.store.BookStore
import shelf.store.LoanStore
import shelf.store.MemberStore

sealed interface LendResult {
    data class Lent(val loan: Loan) : LendResult
    data object UnknownBook : LendResult
    data object UnknownMember : LendResult
    data object BookOnLoan : LendResult
    data object LimitReached : LendResult
}

sealed interface ReturnResult {
    data class Returned(val loan: Loan) : ReturnResult
    data object UnknownLoan : ReturnResult
    data object AlreadyReturned : ReturnResult
}

sealed interface RegisterResult {
    data class Registered(val member: Member) : RegisterResult
    data object EmailTaken : RegisterResult
}

class LendingService(
    private val books: BookStore,
    private val members: MemberStore,
    private val loans: LoanStore,
    private val clock: Clock,                       // injected, so tests can fix "today" (L38)
) {
    fun register(name: String, email: String): RegisterResult =
        if (members.findByEmail(email) != null) RegisterResult.EmailTaken
        else RegisterResult.Registered(members.add(name, email))

    fun member(id: Long): Member? = members.find(id)
    fun loan(id: Long): Loan? = loans.find(id)
    fun loansOf(memberId: Long): List<Loan>? = members.find(memberId)?.let { loans.forMember(memberId) }

    fun lend(bookId: Long, memberId: Long): LendResult {
        books.find(bookId) ?: return LendResult.UnknownBook
        members.find(memberId) ?: return LendResult.UnknownMember
        // Check-then-act across separate store calls: two requests can both pass this check.
        // The partial unique index still refuses the second loan; L46 load-tests and fixes it.
        if (loans.openForBook(bookId) != null) return LendResult.BookOnLoan
        if (loans.forMember(memberId).count { it.isOpen } >= MAX_OPEN_LOANS) return LendResult.LimitReached
        return try {
            LendResult.Lent(loans.add(bookId, memberId, LocalDate.now(clock)))
        } catch (e: BookAlreadyOnLoan) {
            LendResult.BookOnLoan                         // lost the race: same answer as the check above
        }
    }

    fun returnLoan(loanId: Long): ReturnResult {
        val loan = loans.find(loanId) ?: return ReturnResult.UnknownLoan
        val today = LocalDate.now(clock)
        return if (loans.close(loanId, today)) ReturnResult.Returned(loan.copy(returned = today))
        else ReturnResult.AlreadyReturned
    }

    companion object {
        const val MAX_OPEN_LOANS = 3
    }
}
