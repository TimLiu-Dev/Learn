import org.junit.Assert.assertEquals
import org.junit.Test

class PostLoanTest {
    private val neverCalled: (Long, Long) -> LendResult = { _, _ -> throw AssertionError("lend must not be called for an invalid body") }

    @Test fun `a loan is created, with a Location and no null fields`() {
        val r = postLoan(LoanIn(1, 2)) { b, m -> LendResult.Lent(LoanOut(5, b, m, "2026-10-28", null)) }
        assertEquals(201, r.status)
        assertEquals(mapOf("Location" to "/loans/5"), r.headers)
        assertEquals("""{"id":5,"bookId":1,"memberId":2,"loaned":"2026-10-28"}""", r.body)
    }

    @Test fun `missing ids are all reported, before lending`() {
        val r = postLoan(LoanIn(), neverCalled)
        assertEquals(400, r.status)
        assertEquals("application/problem+json", r.type)
        assertEquals("""{"type":"about:blank","title":"Bad Request","status":400,"detail":"2 problem(s) with the request","errors":["bookId is required","memberId is required"]}""", r.body)
    }

    @Test fun `only the missing id is reported`() {
        val r = postLoan(LoanIn(bookId = 1), neverCalled)
        assertEquals("""{"type":"about:blank","title":"Bad Request","status":400,"detail":"1 problem(s) with the request","errors":["memberId is required"]}""", r.body)
    }

    @Test fun `unknown books and members are 404s, without an errors field`() {
        val book = postLoan(LoanIn(1, 2)) { _, _ -> LendResult.UnknownBook }
        assertEquals("""{"type":"about:blank","title":"Not Found","status":404,"detail":"No book with id 1"}""", book.body)
        val member = postLoan(LoanIn(1, 2)) { _, _ -> LendResult.UnknownMember }
        assertEquals(404, member.status)
        assertEquals("""{"type":"about:blank","title":"Not Found","status":404,"detail":"No member with id 2"}""", member.body)
    }

    @Test fun `a lent book and a full card are both 409 Conflict`() {
        val onLoan = postLoan(LoanIn(1, 2)) { _, _ -> LendResult.BookOnLoan }
        assertEquals("""{"type":"about:blank","title":"Conflict","status":409,"detail":"Book 1 is already on loan"}""", onLoan.body)
        val full = postLoan(LoanIn(1, 2)) { _, _ -> LendResult.LimitReached }
        assertEquals(409, full.status)
        assertEquals("application/problem+json", full.type)
        assertEquals("""{"type":"about:blank","title":"Conflict","status":409,"detail":"Member 2 already has 3 open loans"}""", full.body)
    }
}
