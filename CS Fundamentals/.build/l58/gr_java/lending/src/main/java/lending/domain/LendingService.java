package lending.domain;

import java.time.Clock;
import java.time.LocalDate;

// Application service: loads the aggregates, asks them to decide, saves what changed.
public final class LendingService {
    private final Shelf shelf;
    private final Borrowers borrowers;
    private final Clock clock;

    public LendingService(Shelf shelf, Borrowers borrowers, Clock clock) {
        this.shelf = shelf;
        this.borrowers = borrowers;
        this.clock = clock;
    }

    public LendResult lend(CopyId copyId, MemberId memberId) {
        ShelfCopy copy = shelf.find(copyId);
        if (copy == null) return new LendResult.UnknownCopy();
        if (copy.withdrawn()) return new LendResult.NotLendable();
        if (copy.onLoanTo() != null) return new LendResult.CopyOnLoan();
        Borrower borrower = borrowers.find(memberId);
        LendResult result = borrower.lend(copyId, LocalDate.now(clock));
        if (result instanceof LendResult.Lent) {
            borrowers.save(borrower);
            shelf.save(copy.lentTo(memberId));
        }
        return result;
    }

    public ReturnResult giveBack(CopyId copyId, MemberId memberId) {
        Borrower borrower = borrowers.find(memberId);
        ReturnResult result = borrower.giveBack(copyId, LocalDate.now(clock));
        if (result instanceof ReturnResult.Returned) {
            borrowers.save(borrower);
            ShelfCopy copy = shelf.find(copyId);
            if (copy != null) shelf.save(copy.backOnShelf());
        }
        return result;
    }
}
