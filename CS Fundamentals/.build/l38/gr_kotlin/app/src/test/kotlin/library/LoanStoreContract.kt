package library

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// The fake and the real store must behave the same. If the fake drifts, these tests catch it.
abstract class LoanStoreContract {
    abstract fun newStore(): LoanStore
    open fun cleanUp() {}

    protected lateinit var store: LoanStore
    private val oct1: LocalDate = LocalDate.parse("2026-10-01")

    @BeforeTest fun setUp() { store = newStore() }
    @AfterTest fun tearDown() = cleanUp()

    @Test fun `an added loan can be found and is open`() {
        val loan = store.add(bookId = 7, memberId = 1, loaned = oct1)
        assertEquals(loan, store.find(loan.id))
        assertEquals(loan, store.openForBook(7))
        assertEquals(listOf(loan), store.openForMember(1))
    }

    @Test fun `closing a loan records the date and frees the book`() {
        val loan = store.add(7, 1, oct1)
        assertTrue(store.close(loan.id, LocalDate.parse("2026-10-09")))
        assertEquals(LocalDate.parse("2026-10-09"), store.find(loan.id)?.returned)
        assertNull(store.openForBook(7))
        assertFalse(store.close(loan.id, LocalDate.parse("2026-10-10")))
    }

    @Test fun `a returned loan no longer counts for its member`() {
        val first = store.add(7, 1, oct1)
        store.add(8, 1, oct1)
        store.close(first.id, LocalDate.parse("2026-10-09"))
        assertEquals(listOf(8L), store.openForMember(1).map { it.bookId })
    }

    @Test fun `a book cannot have two open loans`() {
        store.add(7, 1, oct1)
        assertFailsWith<Exception> { store.add(7, 2, oct1) }
    }

    @Test fun `unknown ids give null and empty`() {
        assertNull(store.find(42))
        assertNull(store.openForBook(42))
        assertEquals(emptyList(), store.openForMember(42))
    }
}

class FakeLoanStoreTest : LoanStoreContract() {
    override fun newStore() = FakeLoanStore()
}

class SqliteLoanStoreTest : LoanStoreContract() {
    private val file: Path = Files.createTempFile("loans", ".db")
    override fun newStore(): LoanStore {
        val url = "jdbc:sqlite:$file"
        Schema.create(url)
        SqliteMemberStore(url).apply { add("Ada"); add("Alan") }     // foreign keys: members 1 and 2 must exist
        return SqliteLoanStore(url)
    }
    override fun cleanUp() { Files.deleteIfExists(file) }
}
