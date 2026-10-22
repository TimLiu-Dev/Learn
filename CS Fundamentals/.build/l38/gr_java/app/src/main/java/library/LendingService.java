package library;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Pure policy. Every dependency arrives through the constructor; nothing is created in here.
public final class LendingService {
    public static final int MAX_OPEN_LOANS = 3;
    public static final long LOAN_DAYS = 14;
    public static final long FEE_CENTS_PER_DAY = 25;

    private final LoanStore loans;
    private final MemberStore members;
    private final Clock clock;

    public LendingService(LoanStore loans, MemberStore members, Clock clock) {
        this.loans = loans;
        this.members = members;
        this.clock = clock;
    }

    public LendResult lend(long bookId, long memberId) {
        if (members.find(memberId) == null) return new LendResult.UnknownMember();
        LocalDate today = LocalDate.now(clock);
        List<Loan> open = loans.openForMember(memberId);
        if (open.stream().anyMatch(l -> dueDate(l).isBefore(today))) return new LendResult.Blocked("has an overdue loan");
        if (open.size() >= MAX_OPEN_LOANS) return new LendResult.Blocked("already has " + MAX_OPEN_LOANS + " loans");
        if (loans.openForBook(bookId) != null) return new LendResult.BookOnLoan();
        return new LendResult.Lent(loans.add(bookId, memberId, today));
    }

    public ReturnResult returnBook(long loanId) {
        Loan loan = loans.find(loanId);
        if (loan == null) return new ReturnResult.UnknownLoan();
        if (loan.returned() != null) return new ReturnResult.AlreadyReturned();
        LocalDate today = LocalDate.now(clock);
        long daysLate = Math.max(0, ChronoUnit.DAYS.between(dueDate(loan), today));
        loans.close(loanId, today);
        return new ReturnResult.Returned(daysLate * FEE_CENTS_PER_DAY);
    }

    private static LocalDate dueDate(Loan loan) { return loan.loaned().plusDays(LOAN_DAYS); }
}
