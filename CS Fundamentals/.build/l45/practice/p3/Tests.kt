import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanStoreContractTest {
    private val day1 = LocalDate.of(2026, 10, 1)
    private val day2 = LocalDate.of(2026, 10, 2)
    private val store: LoanStore = MemoryLoanStore()

    @Test fun `ids start at 1 and a loan can be found again`() {
        val loan = store.add(7, 1, day1)
        assertEquals(Loan(1, 7, 1, day1, null), loan)
        assertEquals(loan, store.find(1))
        assertNull(store.find(99))
    }

    @Test fun `a closed loan is no longer open, and closes only once`() {
        val loan = store.add(7, 1, day1)
        assertEquals(loan, store.openForBook(7))
        assertTrue("the first close", store.close(loan.id, day2))
        assertNull(store.openForBook(7))
        assertEquals(day2, store.find(loan.id)!!.returned)
        assertFalse("closing it again", store.close(loan.id, day2))
        assertFalse("closing a loan that doesn't exist", store.close(99, day2))
    }

    @Test fun `a second open loan for a book is refused, like the unique index`() {
        store.add(7, 1, day1)
        assertThrows(IllegalStateException::class.java) { store.add(7, 2, day1) }
    }

    @Test fun `a returned book can be lent again`() {
        val first = store.add(7, 1, day1)
        store.close(first.id, day2)
        assertEquals(7L, store.add(7, 2, day2).bookId)
    }

    @Test fun `a member's loans come oldest first, and any loan marks the book`() {
        val first = store.add(7, 1, day1)
        store.add(8, 2, day1)
        val third = store.add(9, 1, day2)
        store.close(first.id, day2)
        assertEquals(listOf(first.id, third.id), store.forMember(1).map { it.id })
        assertTrue("book 7 was lent, even though it's back", store.anyForBook(7))
        assertFalse(store.anyForBook(99))
    }
}
