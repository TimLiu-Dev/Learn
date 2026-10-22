package library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class LendingServiceTest {
    private final FakeLoanStore loans = new FakeLoanStore();
    private final FakeMemberStore members = new FakeMemberStore(new Member(1, "Ada"));

    // A fixed clock: "today" is whatever the test says it is (Clock.fixed, java.time).
    private LendingService on(String day) {
        Clock clock = Clock.fixed(LocalDate.parse(day).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        return new LendingService(loans, members, clock);
    }

    @Test void lendsAFreeBookToAKnownMember() {
        LendResult.Lent lent = assertInstanceOf(LendResult.Lent.class, on("2026-10-01").lend(7, 1));
        assertEquals(LocalDate.parse("2026-10-01"), lent.loan().loaned());
    }

    @Test void anUnknownMemberIsRefused() {
        assertEquals(new LendResult.UnknownMember(), on("2026-10-01").lend(7, 99));
    }

    @Test void aBookOnLoanCannotBeLentAgain() {
        loans.add(7, 2, LocalDate.parse("2026-10-01"));
        assertEquals(new LendResult.BookOnLoan(), on("2026-10-02").lend(7, 1));
    }

    @Test void aFourthLoanIsBlocked() {
        LendingService service = on("2026-10-01");
        for (long book : List.of(1L, 2L, 3L)) service.lend(book, 1);
        assertEquals(new LendResult.Blocked("already has 3 loans"), service.lend(4, 1));
    }

    @Test void anOverdueLoanBlocksNewLoans() {
        loans.add(7, 1, LocalDate.parse("2026-10-01"));                       // due 2026-10-15
        assertInstanceOf(LendResult.Lent.class, on("2026-10-15").lend(8, 1));  // due today: still fine
        assertEquals(new LendResult.Blocked("has an overdue loan"), on("2026-10-16").lend(9, 1));
    }

    @Test void aLateReturnCosts25CentsPerDay() {
        Loan loan = loans.add(7, 1, LocalDate.parse("2026-10-01"));            // due 2026-10-15
        assertEquals(new ReturnResult.Returned(125), on("2026-10-20").returnBook(loan.id()));
    }

    @Test void anOnTimeReturnIsFree() {
        Loan loan = loans.add(7, 1, LocalDate.parse("2026-10-01"));
        assertEquals(new ReturnResult.Returned(0), on("2026-10-15").returnBook(loan.id()));
    }

    @Test void returningTwiceIsReportedNotChargedTwice() {
        Loan loan = loans.add(7, 1, LocalDate.parse("2026-10-01"));
        on("2026-10-20").returnBook(loan.id());
        assertEquals(new ReturnResult.AlreadyReturned(), on("2026-10-21").returnBook(loan.id()));
    }
}
