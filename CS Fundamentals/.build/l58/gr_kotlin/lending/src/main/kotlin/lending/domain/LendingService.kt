package lending.domain

import java.time.Clock
import java.time.LocalDate

// Application service: loads the aggregates, asks them to decide, saves what changed.
class LendingService(private val shelf: Shelf, private val borrowers: Borrowers, private val clock: Clock) {

    fun lend(copyId: CopyId, memberId: MemberId): LendResult {
        val copy = shelf.find(copyId) ?: return LendResult.UnknownCopy
        if (copy.withdrawn) return LendResult.NotLendable
        if (copy.onLoanTo != null) return LendResult.CopyOnLoan
        val borrower = borrowers.find(memberId)
        val result = borrower.lend(copyId, LocalDate.now(clock))
        if (result is LendResult.Lent) {
            borrowers.save(borrower)
            shelf.save(copy.copy(onLoanTo = memberId))
        }
        return result
    }

    fun giveBack(copyId: CopyId, memberId: MemberId): ReturnResult {
        val borrower = borrowers.find(memberId)
        val result = borrower.giveBack(copyId, LocalDate.now(clock))
        if (result is ReturnResult.Returned) {
            borrowers.save(borrower)
            shelf.find(copyId)?.let { shelf.save(it.copy(onLoanTo = null)) }
        }
        return result
    }
}
