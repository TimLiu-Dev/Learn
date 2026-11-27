package lending.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

// The aggregate root for one member's borrowing. Every change to their loans goes through
// these methods, so the rules (at most 3 open loans, none overdue) can't be bypassed.
class Borrower(val id: MemberId, history: List<Loan> = emptyList()) {
    private val loans = history.toMutableList()

    val history: List<Loan> get() = loans.toList()          // copies: callers can't edit ours
    val openLoans: List<Loan> get() = loans.filter { it.isOpen }

    fun lend(copy: CopyId, today: LocalDate): LendResult {
        val open = openLoans
        return when {
            open.any { it.due < today } -> LendResult.Blocked("has an overdue loan")
            open.size >= MAX_OPEN_LOANS -> LendResult.Blocked("already has $MAX_OPEN_LOANS loans")
            else -> Loan(copy, today).let { loans += it; LendResult.Lent(it) }
        }
    }

    fun giveBack(copy: CopyId, today: LocalDate): ReturnResult {
        val i = loans.indexOfFirst { it.copy == copy && it.isOpen }
        if (i < 0) return ReturnResult.NotBorrowed
        val loan = loans[i]
        loans[i] = loan.copy(returned = today)
        val daysLate = maxOf(0, ChronoUnit.DAYS.between(loan.due, today))
        return ReturnResult.Returned(daysLate * FEE_CENTS_PER_DAY)
    }

    companion object {
        const val MAX_OPEN_LOANS = 3
        const val LOAN_DAYS = 14L
        const val FEE_CENTS_PER_DAY = 25L
    }
}
