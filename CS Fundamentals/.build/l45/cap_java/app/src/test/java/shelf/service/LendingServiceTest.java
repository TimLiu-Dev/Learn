package shelf.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shelf.domain.Loan;
import shelf.service.LendingService.LendResult;
import shelf.service.LendingService.RegisterResult;
import shelf.service.LendingService.ReturnResult;
import shelf.store.MemoryBookStore;
import shelf.store.MemoryLoanStore;
import shelf.store.MemoryMemberStore;

// Unit tests: the real service, in-memory fakes, a fixed clock. No database, no HTTP.
class LendingServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC);
    private final MemoryBookStore books = new MemoryBookStore();
    private final MemoryMemberStore members = new MemoryMemberStore();
    private final MemoryLoanStore loans = new MemoryLoanStore();
    private final LendingService service = new LendingService(books, members, loans, clock);
    private long ada;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 5; i++) books.add("Book " + i, "Author", null);   // ids 1..5
        ada = members.add("Ada", "ada@example.com").id();
    }

    private Loan lent(long bookId, long memberId) {
        return assertInstanceOf(LendResult.Lent.class, service.lend(bookId, memberId)).loan();
    }

    @Test
    void lendingRecordsTodaysDateFromTheClock() {
        assertEquals(LocalDate.of(2026, 10, 27), lent(1, ada).loaned());
    }

    @Test
    void aBookCanOnlyBeOnOneLoanAtATime() {
        lent(1, ada);
        long alan = members.add("Alan", "alan@example.com").id();
        assertInstanceOf(LendResult.BookOnLoan.class, service.lend(1, alan));
    }

    @Test
    void unknownBooksAndMembersAreReported() {
        assertInstanceOf(LendResult.UnknownBook.class, service.lend(99, ada));
        assertInstanceOf(LendResult.UnknownMember.class, service.lend(1, 99));
    }

    @Test
    void aMemberCanHoldAtMostThreeOpenLoans() {
        for (long book = 1; book <= 3; book++) lent(book, ada);
        assertInstanceOf(LendResult.LimitReached.class, service.lend(4, ada));
    }

    @Test
    void returningFreesTheBookAndTheMembersSlot() {
        Loan loan = lent(1, ada);
        var returned = assertInstanceOf(ReturnResult.Returned.class, service.returnLoan(loan.id()));
        assertEquals(LocalDate.of(2026, 10, 27), returned.loan().returned());
        lent(1, ada);
    }

    @Test
    void aLoanCanOnlyBeReturnedOnce() {
        Loan loan = lent(1, ada);
        service.returnLoan(loan.id());
        assertInstanceOf(ReturnResult.AlreadyReturned.class, service.returnLoan(loan.id()));
        assertInstanceOf(ReturnResult.UnknownLoan.class, service.returnLoan(99));
    }

    @Test
    void anEmailCanOnlyBeRegisteredOnce() {
        assertInstanceOf(RegisterResult.EmailTaken.class, service.register("Ada again", "ada@example.com"));
    }
}
