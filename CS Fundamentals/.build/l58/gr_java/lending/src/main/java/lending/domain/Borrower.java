package lending.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// The aggregate root for one member's borrowing. Every change to their loans goes through
// these methods, so the rules (at most 3 open loans, none overdue) can't be bypassed.
public final class Borrower {
    public static final int MAX_OPEN_LOANS = 3;
    public static final long LOAN_DAYS = 14;
    public static final long FEE_CENTS_PER_DAY = 25;

    private final MemberId id;
    private final List<Loan> loans;

    public Borrower(MemberId id, List<Loan> history) {
        this.id = id;
        this.loans = new ArrayList<>(history);
    }

    public MemberId id() { return id; }
    public List<Loan> history() { return List.copyOf(loans); }          // copies: callers can't edit ours
    public List<Loan> openLoans() { return loans.stream().filter(Loan::isOpen).toList(); }

    public LendResult lend(CopyId copy, LocalDate today) {
        List<Loan> open = openLoans();
        if (open.stream().anyMatch(l -> l.due().isBefore(today))) return new LendResult.Blocked("has an overdue loan");
        if (open.size() >= MAX_OPEN_LOANS) return new LendResult.Blocked("already has " + MAX_OPEN_LOANS + " loans");
        Loan loan = new Loan(copy, today, null);
        loans.add(loan);
        return new LendResult.Lent(loan);
    }

    public ReturnResult giveBack(CopyId copy, LocalDate today) {
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            if (loan.copy().equals(copy) && loan.isOpen()) {
                loans.set(i, loan.withReturned(today));
                long daysLate = Math.max(0, ChronoUnit.DAYS.between(loan.due(), today));
                return new ReturnResult.Returned(daysLate * FEE_CENTS_PER_DAY);
            }
        }
        return new ReturnResult.NotBorrowed();
    }
}
