package lending.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lending.domain.Borrower;
import lending.domain.Borrowers;
import lending.domain.Loan;
import lending.domain.MemberId;

// Stores plain loan lists and rebuilds the aggregate on every find, like a database would,
// so no caller ever shares a live Borrower with another.
public final class InMemoryBorrowers implements Borrowers {
    private final Map<MemberId, List<Loan>> loans = new HashMap<>();

    @Override public Borrower find(MemberId id) { return new Borrower(id, loans.getOrDefault(id, List.of())); }
    @Override public void save(Borrower borrower) { loans.put(borrower.id(), borrower.history()); }
}
