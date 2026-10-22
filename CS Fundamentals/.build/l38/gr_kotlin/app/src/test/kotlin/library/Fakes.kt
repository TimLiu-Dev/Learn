package library

import java.time.LocalDate

// A fake: a real, working implementation that takes a shortcut (a list in memory instead of SQLite).
class FakeLoanStore : LoanStore {
    private val loans = mutableListOf<Loan>()

    override fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan {
        check(openForBook(bookId) == null) { "book $bookId already has an open loan" }   // like the unique index
        return Loan(loans.size + 1L, bookId, memberId, loaned, null).also { loans += it }
    }

    override fun find(loanId: Long) = loans.find { it.id == loanId }
    override fun openForMember(memberId: Long) = loans.filter { it.memberId == memberId && it.returned == null }
    override fun openForBook(bookId: Long) = loans.find { it.bookId == bookId && it.returned == null }

    override fun close(loanId: Long, returned: LocalDate): Boolean {
        val i = loans.indexOfFirst { it.id == loanId && it.returned == null }
        if (i < 0) return false
        loans[i] = loans[i].copy(returned = returned)
        return true
    }
}

class FakeMemberStore(vararg members: Member) : MemberStore {
    private val byId = members.associateBy { it.id }
    override fun find(memberId: Long) = byId[memberId]
}
