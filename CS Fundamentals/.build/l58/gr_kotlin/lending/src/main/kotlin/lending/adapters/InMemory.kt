package lending.adapters

import lending.domain.Borrower
import lending.domain.Borrowers
import lending.domain.CopyId
import lending.domain.Loan
import lending.domain.MemberId
import lending.domain.Shelf
import lending.domain.ShelfCopy

class InMemoryShelf : Shelf {
    private val copies = HashMap<CopyId, ShelfCopy>()
    override fun find(id: CopyId) = copies[id]
    override fun save(copy: ShelfCopy) { copies[copy.id] = copy }
}

// Stores plain loan lists and rebuilds the aggregate on every find, like a database would,
// so no caller ever shares a live Borrower with another.
class InMemoryBorrowers : Borrowers {
    private val loans = HashMap<MemberId, List<Loan>>()
    override fun find(id: MemberId) = Borrower(id, loans[id].orEmpty())
    override fun save(borrower: Borrower) { loans[borrower.id] = borrower.history }
}
