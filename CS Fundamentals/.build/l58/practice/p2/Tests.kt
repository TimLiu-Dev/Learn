import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BorrowerTest {
    private val day = LocalDate.of(2026, 11, 2)

    @Test fun `three loans are fine, a fourth is blocked`() {
        val b = Borrower(1)
        assertEquals("Lent C-1", b.lend("C-1", day))
        b.lend("C-2", day); b.lend("C-3", day)
        assertEquals("Blocked: already has 3 loans", b.lend("C-4", day))
        assertEquals(3, b.openLoans.size)
    }

    @Test fun `an overdue loan blocks the next one`() {
        val b = Borrower(1)
        b.lend("C-1", day)
        assertEquals("Lent C-2", b.lend("C-2", day.plusDays(14)))
        assertEquals("Blocked: has an overdue loan", b.lend("C-3", day.plusDays(15)))
    }

    @Test fun `the same copy can't be borrowed twice`() {
        val b = Borrower(1)
        b.lend("C-1", day)
        assertEquals("Blocked: already has C-1", b.lend("C-1", day))
    }

    @Test fun `giving a copy back frees a slot`() {
        val b = Borrower(1)
        listOf("C-1", "C-2", "C-3").forEach { b.lend(it, day) }
        assertTrue(b.giveBack("C-2", day.plusDays(3)))
        assertFalse("C-2 is already back", b.giveBack("C-2", day.plusDays(4)))
        assertEquals("Lent C-4", b.lend("C-4", day.plusDays(4)))
        assertEquals(listOf("C-1", "C-3", "C-4"), b.openLoans.map { it.copy })
    }

    @Test fun `editing the list it hands out changes nothing inside`() {
        val b = Borrower(1)
        b.lend("C-1", day)
        runCatching { (b.openLoans as MutableList<Loan>).add(Loan("C-9", day)) }   // a caller trying to cheat
        assertEquals("only the real loan is open", listOf("C-1"), b.openLoans.map { it.copy })
    }
}
