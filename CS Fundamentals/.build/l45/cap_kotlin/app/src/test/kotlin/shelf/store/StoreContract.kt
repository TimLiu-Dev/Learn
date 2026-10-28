package shelf.store

import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Contract tests (L38): one suite, run against every implementation, so the fakes can't drift from SQLite.
abstract class StoreContract {
    protected abstract fun newStores(): Triple<BookStore, MemberStore, LoanStore>

    private lateinit var books: BookStore
    private lateinit var members: MemberStore
    private lateinit var loans: LoanStore
    private val day1 = LocalDate.of(2026, 10, 1)
    private val day2 = LocalDate.of(2026, 10, 2)

    @BeforeTest
    fun setUp() {
        val (b, m, l) = newStores()
        books = b; members = m; loans = l
    }

    @Test
    fun `books round-trip, including a missing year`() {
        val dune = books.add("Dune", "Frank Herbert", 1965)
        val beowulf = books.add("Beowulf", "Anonymous", null)
        assertEquals(dune, books.find(dune.id))
        assertNull(books.find(beowulf.id)!!.year)
        assertNull(books.find(999))
    }

    @Test
    fun `list is ordered by id, paged and filtered by author`() {
        val ids = listOf("A" to "Le Guin", "B" to "Herbert", "C" to "Le Guin", "D" to "Le Guin")
            .map { (t, a) -> books.add(t, a, null).id }
        assertEquals(listOf("B", "C"), books.list(2, 1, null).map { it.title })
        assertEquals(listOf("C", "D"), books.list(10, 1, "Le Guin").map { it.title })
        assertEquals(4, books.count(null))
        assertEquals(3, books.count("Le Guin"))
        assertEquals(ids.sorted(), books.list(10, 0, null).map { it.id })
    }

    @Test
    fun `update and delete report missing books`() {
        val id = books.add("Dnue", "Herbert", 1965).id
        assertEquals("Dune", books.update(id, "Dune", "Frank Herbert", 1965)!!.title)
        assertEquals("Frank Herbert", books.find(id)!!.author)
        assertNull(books.update(999, "X", "Y", null))
        assertTrue(books.delete(id))
        assertFalse(books.delete(id))
    }

    @Test
    fun `members are found by id and by email`() {
        val ada = members.add("Ada", "ada@example.com")
        assertEquals(ada, members.find(ada.id))
        assertEquals(ada, members.findByEmail("ada@example.com"))
        assertNull(members.findByEmail("nobody@example.com"))
    }

    @Test
    fun `a closed loan is no longer open, and closes only once`() {
        val book = books.add("Dune", "Herbert", 1965).id
        val ada = members.add("Ada", "ada@example.com").id
        val loan = loans.add(book, ada, day1)
        assertEquals(loan, loans.openForBook(book))
        assertTrue(loans.close(loan.id, day2))
        assertNull(loans.openForBook(book))
        assertEquals(day2, loans.find(loan.id)!!.returned)
        assertFalse(loans.close(loan.id, day2))
    }

    @Test
    fun `a member's loans come oldest first, and any loan marks the book`() {
        val b1 = books.add("Dune", "Herbert", 1965).id
        val b2 = books.add("Emma", "Austen", 1815).id
        val ada = members.add("Ada", "ada@example.com").id
        val first = loans.add(b1, ada, day1)
        loans.close(first.id, day2)
        val second = loans.add(b2, ada, day2)
        assertEquals(listOf(first.id, second.id), loans.forMember(ada).map { it.id })
        assertTrue(loans.anyForBook(b1))
        assertFalse(loans.anyForBook(999))
    }
}

class MemoryStoreTest : StoreContract() {
    override fun newStores() = Triple(MemoryBookStore(), MemoryMemberStore(), MemoryLoanStore())
}
