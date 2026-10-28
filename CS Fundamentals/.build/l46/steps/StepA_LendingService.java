package shelf.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import shelf.domain.Loan;
import shelf.domain.Member;
import shelf.store.BookAlreadyOnLoan;
import shelf.store.BookStore;
import shelf.store.LoanStore;
import shelf.store.MemberStore;

public final class LendingService {
    public static final int MAX_OPEN_LOANS = 3;

    public sealed interface LendResult {
        record Lent(Loan loan) implements LendResult {}
        record UnknownBook() implements LendResult {}
        record UnknownMember() implements LendResult {}
        record BookOnLoan() implements LendResult {}
        record LimitReached() implements LendResult {}
    }

    public sealed interface ReturnResult {
        record Returned(Loan loan) implements ReturnResult {}
        record UnknownLoan() implements ReturnResult {}
        record AlreadyReturned() implements ReturnResult {}
    }

    public sealed interface RegisterResult {
        record Registered(Member member) implements RegisterResult {}
        record EmailTaken() implements RegisterResult {}
    }

    private final BookStore books;
    private final MemberStore members;
    private final LoanStore loans;
    private final Clock clock;                       // injected, so tests can fix "today" (L38)

    public LendingService(BookStore books, MemberStore members, LoanStore loans, Clock clock) {
        this.books = books;
        this.members = members;
        this.loans = loans;
        this.clock = clock;
    }

    public RegisterResult register(String name, String email) {
        if (members.findByEmail(email).isPresent()) return new RegisterResult.EmailTaken();
        return new RegisterResult.Registered(members.add(name, email));
    }

    public Optional<Member> member(long id) {
        return members.find(id);
    }

    public Optional<Loan> loan(long id) {
        return loans.find(id);
    }

    public Optional<List<Loan>> loansOf(long memberId) {
        return members.find(memberId).map(m -> loans.forMember(memberId));
    }

    public LendResult lend(long bookId, long memberId) {
        if (books.find(bookId).isEmpty()) return new LendResult.UnknownBook();
        if (members.find(memberId).isEmpty()) return new LendResult.UnknownMember();
        // Check-then-act across separate store calls: two requests can both pass this check.
        // The partial unique index still refuses the second loan; L46 load-tests and fixes it.
        if (loans.openForBook(bookId).isPresent()) return new LendResult.BookOnLoan();
        long open = loans.forMember(memberId).stream().filter(Loan::isOpen).count();
        if (open >= MAX_OPEN_LOANS) return new LendResult.LimitReached();
        try {
            return new LendResult.Lent(loans.add(bookId, memberId, LocalDate.now(clock)));
        } catch (BookAlreadyOnLoan e) {
            return new LendResult.BookOnLoan();         // lost the race: same answer as the check above
        }
    }

    public ReturnResult returnLoan(long loanId) {
        Optional<Loan> loan = loans.find(loanId);
        if (loan.isEmpty()) return new ReturnResult.UnknownLoan();
        LocalDate today = LocalDate.now(clock);
        return loans.close(loanId, today)
            ? new ReturnResult.Returned(loan.get().withReturned(today))
            : new ReturnResult.AlreadyReturned();
    }
}
