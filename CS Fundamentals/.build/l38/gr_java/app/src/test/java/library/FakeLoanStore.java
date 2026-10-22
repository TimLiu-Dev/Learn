package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// A fake: a real, working implementation that takes a shortcut (a list in memory instead of SQLite).
final class FakeLoanStore implements LoanStore {
    private final List<Loan> loans = new ArrayList<>();

    public Loan add(long bookId, long memberId, LocalDate loaned) {
        if (openForBook(bookId) != null) throw new IllegalStateException("book " + bookId + " already has an open loan");
        Loan loan = new Loan(loans.size() + 1L, bookId, memberId, loaned, null);
        loans.add(loan);
        return loan;
    }

    public Loan find(long loanId) {
        return loans.stream().filter(l -> l.id() == loanId).findFirst().orElse(null);
    }

    public List<Loan> openForMember(long memberId) {
        return loans.stream().filter(l -> l.memberId() == memberId && l.returned() == null).toList();
    }

    public Loan openForBook(long bookId) {
        return loans.stream().filter(l -> l.bookId() == bookId && l.returned() == null).findFirst().orElse(null);
    }

    public boolean close(long loanId, LocalDate returned) {
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.id() == loanId && l.returned() == null) {
                loans.set(i, l.withReturned(returned));
                return true;
            }
        }
        return false;
    }
}
