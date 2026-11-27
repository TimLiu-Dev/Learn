package shelf.store

import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
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

    @Test
    fun `tryLend refuses a book that is out and a member at the limit`() {
        val (b1, b2) = books.add("Dune", "Herbert", 1965).id to books.add("Emma", "Austen", 1815).id
        val (ada, bob) = members.add("Ada", "ada@example.com").id to members.add("Bob", "bob@example.com").id
        assertTrue(loans.tryLend(b1, ada, day1, maxOpen = 1) is TryLend.Lent)
        assertEquals(TryLend.BookTaken, loans.tryLend(b1, bob, day1, maxOpen = 1))
        assertEquals(TryLend.AtLimit, loans.tryLend(b2, ada, day1, maxOpen = 1))
    }

    // L46: v1 checked in the service and inserted later, so a burst of requests could break both rules.
    @Test
    fun `tryLend keeps both rules under a burst of concurrent calls`() {
        val book = books.add("Dune", "Herbert", 1965).id
        val crowd = (1..20).map { members.add("M$it", "m$it@example.com").id }
        val sameBook = burst(20) { i -> loans.tryLend(book, crowd[i], day1, maxOpen = 3) }
        assertEquals(1, sameBook.count { it is TryLend.Lent })

        val greedy = members.add("Greedy", "greedy@example.com").id
        val shelf = (1..10).map { books.add("Book $it", "Anon", null).id }
        val oneMember = burst(10) { i -> loans.tryLend(shelf[i], greedy, day1, maxOpen = 3) }
        assertEquals(3, oneMember.count { it is TryLend.Lent })
        assertEquals(3, loans.forMember(greedy).count { it.isOpen })
    }

    @Test
    fun `tryAdd lets exactly one of ten simultaneous sign-ups with one email through`() {
        val results = burst(10) { members.tryAdd("Ada", "ada@example.com") }
        assertEquals(1, results.count { it != null })
        assertEquals(results.single { it != null }, members.findByEmail("ada@example.com"))
    }

    private fun <T> burst(n: Int, call: (Int) -> T): List<T> {
        val start = CountDownLatch(1)
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            val futures = (0 until n).map { i -> pool.submit<T> { start.await(); call(i) } }
            start.countDown()
            return futures.map { it.get() }
        }
    }
}

class MemoryStoreTest : StoreContract() {
    override fun newStores() = Triple(MemoryBookStore(), MemoryMemberStore(), MemoryLoanStore())
}
