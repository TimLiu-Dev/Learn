package shelf.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import shelf.domain.Loan;

public interface LoanStore {
    Loan add(long bookId, long memberId, LocalDate loaned);
    Optional<Loan> find(long id);
    Optional<Loan> openForBook(long bookId);
    List<Loan> forMember(long memberId);                       // oldest first
    boolean anyForBook(long bookId);
    boolean close(long id, LocalDate returned);                // false: missing or already closed
    /** Check both lending rules and insert the loan as ONE atomic step. */
    TryLend tryLend(long bookId, long memberId, LocalDate loaned, int maxOpen);

    sealed interface TryLend {
        record Lent(Loan loan) implements TryLend {}
        record BookTaken() implements TryLend {}
        record AtLimit() implements TryLend {}
    }
}
