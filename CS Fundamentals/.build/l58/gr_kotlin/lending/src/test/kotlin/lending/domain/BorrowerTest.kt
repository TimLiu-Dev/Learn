package lending.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BorrowerTest {
    private val day1 = LocalDate.of(2026, 11, 2)

    @Test fun `a fourth open loan is blocked`() {
        val b = Borrower(MemberId(1))
        repeat(3) { assertIs<LendResult.Lent>(b.lend(CopyId("C-$it"), day1)) }
        assertEquals(LendResult.Blocked("already has 3 loans"), b.lend(CopyId("C-9"), day1))
    }

    @Test fun `an overdue loan blocks new loans`() {
        val b = Borrower(MemberId(1))
        b.lend(CopyId("C-1"), day1)
        assertEquals(LendResult.Blocked("has an overdue loan"), b.lend(CopyId("C-2"), day1.plusDays(15)))
    }

    @Test fun `a late return costs 25 cents per day`() {
        val b = Borrower(MemberId(1))
        b.lend(CopyId("C-1"), day1)
        assertEquals(ReturnResult.Returned(75), b.giveBack(CopyId("C-1"), day1.plusDays(17)))
        assertEquals(ReturnResult.NotBorrowed, b.giveBack(CopyId("C-1"), day1.plusDays(18)))
    }

    @Test fun `the history it hands out is a snapshot, not its own list`() {
        val b = Borrower(MemberId(1))
        b.lend(CopyId("C-1"), day1)
        val seen = b.history
        b.lend(CopyId("C-2"), day1)
        assertEquals(1, seen.size, "a caller's list must not change under it")
        assertEquals(2, b.history.size)
    }
}
