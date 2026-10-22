package library

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LendingServiceTest {
    private val ada = Member(1, "Ada")
    private val loans = FakeLoanStore()
    private val members = FakeMemberStore(ada)

    // A fixed clock: "today" is whatever the test says it is (Clock.fixed, java.time).
    private fun on(day: String) = LendingService(loans, members,
        Clock.fixed(LocalDate.parse(day).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC))

    @Test fun `lends a free book to a known member`() {
        val result = on("2026-10-01").lend(bookId = 7, memberId = 1)
        assertIs<LendResult.Lent>(result)
        assertEquals(LocalDate.parse("2026-10-01"), result.loan.loaned)
    }

    @Test fun `an unknown member is refused`() =
        assertEquals(LendResult.UnknownMember, on("2026-10-01").lend(7, memberId = 99))

    @Test fun `a book on loan cannot be lent again`() {
        loans.add(7, 2, LocalDate.parse("2026-10-01"))
        assertEquals(LendResult.BookOnLoan, on("2026-10-02").lend(7, 1))
    }

    @Test fun `a fourth loan is blocked`() {
        val service = on("2026-10-01")
        listOf(1L, 2L, 3L).forEach { service.lend(it, 1) }
        assertEquals(LendResult.Blocked("already has 3 loans"), service.lend(4, 1))
    }

    @Test fun `an overdue loan blocks new loans`() {
        loans.add(7, 1, LocalDate.parse("2026-10-01"))              // due 2026-10-15
        assertIs<LendResult.Lent>(on("2026-10-15").lend(8, 1))      // due today: still fine
        assertEquals(LendResult.Blocked("has an overdue loan"), on("2026-10-16").lend(9, 1))
    }

    @Test fun `a late return costs 25 cents per day`() {
        val loan = loans.add(7, 1, LocalDate.parse("2026-10-01"))   // due 2026-10-15
        assertEquals(ReturnResult.Returned(feeCents = 125), on("2026-10-20").returnBook(loan.id))
    }

    @Test fun `an on-time return is free`() {
        val loan = loans.add(7, 1, LocalDate.parse("2026-10-01"))
        assertEquals(ReturnResult.Returned(feeCents = 0), on("2026-10-15").returnBook(loan.id))
    }

    @Test fun `returning twice is reported, not charged twice`() {
        val loan = loans.add(7, 1, LocalDate.parse("2026-10-01"))
        on("2026-10-20").returnBook(loan.id)
        assertEquals(ReturnResult.AlreadyReturned, on("2026-10-21").returnBook(loan.id))
    }
}
