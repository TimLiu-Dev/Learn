package desk

import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoanDeskTest {
    private val desk = LoanDesk()
    private val dune = desk.addBook("Dune")
    private val ada = desk.addMember("Ada")
    private val linus = desk.addMember("Linus")

    @Test fun `lending a free book works`() {
        assertEquals(LendResult.Lent, desk.lend(dune, ada))
        assertEquals("Ada", desk.borrowerOf(dune))
    }

    @Test fun `a second lender cannot overwrite the first`() {
        desk.lend(dune, ada)
        assertEquals(LendResult.AlreadyOnLoan, desk.lend(dune, linus))
        assertEquals("Ada", desk.borrowerOf(dune))
    }

    @Test fun `unknown book and member are reported`() {
        assertEquals(LendResult.NoSuchBook, desk.lend(99, ada))
        assertEquals(LendResult.NoSuchMember, desk.lend(dune, 99))
    }

    @Test fun `giving back twice is harmless`() {
        desk.lend(dune, ada)
        desk.giveBack(dune)
        desk.giveBack(dune)
        assertNull(desk.borrowerOf(dune))
        assertEquals(LendResult.Lent, desk.lend(dune, linus))
    }

    @Test fun `the public interface is five methods`() {
        val names = LoanDesk::class.java.declaredMethods.filter { Modifier.isPublic(it.modifiers) }.map { it.name }.sorted()
        assertEquals(listOf("addBook", "addMember", "borrowerOf", "giveBack", "lend"), names)
    }
}
