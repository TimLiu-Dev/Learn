package library

import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed interface LendResult {
    data class Lent(val loan: Loan) : LendResult
    data class Blocked(val reason: String) : LendResult
    data object BookOnLoan : LendResult
    data object UnknownMember : LendResult
}

sealed interface ReturnResult {
    data class Returned(val feeCents: Long) : ReturnResult
    data object AlreadyReturned : ReturnResult
    data object UnknownLoan : ReturnResult
}

// Pure policy. Every dependency arrives through the constructor; nothing is created in here.
class LendingService(
    private val loans: LoanStore,
    private val members: MemberStore,
    private val clock: Clock,
) {
    fun lend(bookId: Long, memberId: Long): LendResult {
        members.find(memberId) ?: return LendResult.UnknownMember
        val today = LocalDate.now(clock)
        val open = loans.openForMember(memberId)
        return when {
            open.any { dueDate(it) < today } -> LendResult.Blocked("has an overdue loan")
            open.size >= MAX_OPEN_LOANS -> LendResult.Blocked("already has $MAX_OPEN_LOANS loans")
            loans.openForBook(bookId) != null -> LendResult.BookOnLoan
            else -> LendResult.Lent(loans.add(bookId, memberId, today))
        }
    }

    fun returnBook(loanId: Long): ReturnResult {
        val loan = loans.find(loanId) ?: return ReturnResult.UnknownLoan
        if (loan.returned != null) return ReturnResult.AlreadyReturned
        val today = LocalDate.now(clock)
        val daysLate = maxOf(0, ChronoUnit.DAYS.between(dueDate(loan), today))
        loans.close(loanId, today)
        return ReturnResult.Returned(daysLate * FEE_CENTS_PER_DAY)
    }

    private fun dueDate(loan: Loan): LocalDate = loan.loaned.plusDays(LOAN_DAYS)

    companion object {
        const val MAX_OPEN_LOANS = 3
        const val LOAN_DAYS = 14L
        const val FEE_CENTS_PER_DAY = 25L
    }
}
