package lending.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BorrowerTest {
    private final LocalDate day1 = LocalDate.of(2026, 11, 2);
    private final Borrower b = new Borrower(new MemberId(1), List.of());

    @Test
    void aFourthOpenLoanIsBlocked() {
        for (int i = 0; i < 3; i++) assertInstanceOf(LendResult.Lent.class, b.lend(new CopyId("C-" + i), day1));
        assertEquals(new LendResult.Blocked("already has 3 loans"), b.lend(new CopyId("C-9"), day1));
    }

    @Test
    void anOverdueLoanBlocksNewLoans() {
        b.lend(new CopyId("C-1"), day1);
        assertEquals(new LendResult.Blocked("has an overdue loan"), b.lend(new CopyId("C-2"), day1.plusDays(15)));
    }

    @Test
    void aLateReturnCosts25CentsPerDay() {
        b.lend(new CopyId("C-1"), day1);
        assertEquals(new ReturnResult.Returned(75), b.giveBack(new CopyId("C-1"), day1.plusDays(17)));
        assertEquals(new ReturnResult.NotBorrowed(), b.giveBack(new CopyId("C-1"), day1.plusDays(18)));
    }

    @Test
    void theHistoryItHandsOutIsASnapshotNotItsOwnList() {
        b.lend(new CopyId("C-1"), day1);
        List<Loan> seen = b.history();
        b.lend(new CopyId("C-2"), day1);
        assertEquals(1, seen.size(), "a caller's list must not change under it");
        assertEquals(2, b.history().size());
    }
}
