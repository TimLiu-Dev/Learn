package shelf.service

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import shelf.store.MemoryBookStore
import shelf.store.MemoryLoanStore
import shelf.store.MemoryMemberStore

// Unit tests: the real service, in-memory fakes, a fixed clock. No database, no HTTP.
class LendingServiceTest {
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)
    private val books = MemoryBookStore()
    private val members = MemoryMemberStore()
    private val loans = MemoryLoanStore()
    private val service = LendingService(books, members, loans, clock)
    private var ada = 0L

    @BeforeTest
    fun setUp() {
        repeat(5) { books.add("Book $it", "Author", null) }        // ids 1..5
        ada = members.add("Ada", "ada@example.com").id
    }

    @Test
    fun `lending records today's date from the clock`() {
        val lent = assertIs<LendResult.Lent>(service.lend(1, ada))
        assertEquals(LocalDate.of(2026, 10, 27), lent.loan.loaned)
    }

    @Test
    fun `a book can only be on one loan at a time`() {
        service.lend(1, ada)
        val alan = members.add("Alan", "alan@example.com").id
        assertEquals(LendResult.BookOnLoan, service.lend(1, alan))
    }

    @Test
    fun `unknown books and members are reported`() {
        assertEquals(LendResult.UnknownBook, service.lend(99, ada))
        assertEquals(LendResult.UnknownMember, service.lend(1, 99))
    }

    @Test
    fun `a member can hold at most three open loans`() {
        (1L..3L).forEach { assertIs<LendResult.Lent>(service.lend(it, ada)) }
        assertEquals(LendResult.LimitReached, service.lend(4, ada))
    }

    @Test
    fun `returning frees the book and the member's slot`() {
        val loan = assertIs<LendResult.Lent>(service.lend(1, ada)).loan
        val returned = assertIs<ReturnResult.Returned>(service.returnLoan(loan.id))
        assertEquals(LocalDate.of(2026, 10, 27), returned.loan.returned)
        assertIs<LendResult.Lent>(service.lend(1, ada))
    }

    @Test
    fun `a loan can only be returned once`() {
        val loan = assertIs<LendResult.Lent>(service.lend(1, ada)).loan
        service.returnLoan(loan.id)
        assertEquals(ReturnResult.AlreadyReturned, service.returnLoan(loan.id))
        assertEquals(ReturnResult.UnknownLoan, service.returnLoan(99))
    }

    @Test
    fun `an email can only be registered once`() {
        assertEquals(RegisterResult.EmailTaken, service.register("Ada again", "ada@example.com"))
    }
}
