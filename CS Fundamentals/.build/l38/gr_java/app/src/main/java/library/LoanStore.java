package library;

import java.time.LocalDate;
import java.util.List;

// The abstractions the POLICY needs (DIP). They live next to LendingService, not next to SQLite.
public interface LoanStore {
    Loan add(long bookId, long memberId, LocalDate loaned);
    Loan find(long loanId);                  // null if missing
    List<Loan> openForMember(long memberId);
    Loan openForBook(long bookId);           // null if the book is free
    boolean close(long loanId, LocalDate returned);
}
