package shelf.service

import java.time.Clock
import java.time.LocalDate
import shelf.domain.Loan
import shelf.domain.Member
import shelf.store.BookStore
import shelf.store.LoanStore
import shelf.store.MemberStore
import shelf.store.TryLend

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
    // v1 checked findByEmail, then added: a burst of sign-ups with one email got 500s (Exercise 1).
    fun register(name: String, email: String): RegisterResult =
        members.tryAdd(name, email)?.let { RegisterResult.Registered(it) } ?: RegisterResult.EmailTaken

    fun member(id: Long): Member? = members.find(id)
    fun loan(id: Long): Loan? = loans.find(id)
    fun loansOf(memberId: Long): List<Loan>? = members.find(memberId)?.let { loans.forMember(memberId) }

    fun lend(bookId: Long, memberId: Long): LendResult {
        books.find(bookId) ?: return LendResult.UnknownBook
        members.find(memberId) ?: return LendResult.UnknownMember
        // v1 checked the rules here and inserted later: two requests could both pass the checks.
        // v2 hands both rules to the store, which checks and inserts in one transaction.
        return when (val r = loans.tryLend(bookId, memberId, LocalDate.now(clock), MAX_OPEN_LOANS)) {
            is TryLend.Lent -> LendResult.Lent(r.loan)
            TryLend.BookTaken -> LendResult.BookOnLoan
            TryLend.AtLimit -> LendResult.LimitReached
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
