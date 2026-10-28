package shelf.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import shelf.domain.Loan;

public final class MemoryLoanStore implements LoanStore {
    private final TreeMap<Long, Loan> loans = new TreeMap<>();
    private long nextId = 1;

    @Override
    public synchronized Loan add(long bookId, long memberId, LocalDate loaned) {
        Loan loan = new Loan(nextId++, bookId, memberId, loaned, null);
        loans.put(loan.id(), loan);
        return loan;
    }

    @Override
    public synchronized Optional<Loan> find(long id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override
    public synchronized Optional<Loan> openForBook(long bookId) {
        return loans.values().stream().filter(l -> l.bookId() == bookId && l.isOpen()).findFirst();
    }

    @Override
    public synchronized List<Loan> forMember(long memberId) {
        return loans.values().stream().filter(l -> l.memberId() == memberId).toList();
    }

    @Override
    public synchronized boolean anyForBook(long bookId) {
        return loans.values().stream().anyMatch(l -> l.bookId() == bookId);
    }

    @Override
    public synchronized boolean close(long id, LocalDate returned) {
        Loan loan = loans.get(id);
        if (loan == null || !loan.isOpen()) return false;
        loans.put(id, loan.withReturned(returned));
        return true;
    }
}
