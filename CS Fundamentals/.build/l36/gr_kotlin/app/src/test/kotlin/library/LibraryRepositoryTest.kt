package library

import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LibraryRepositoryTest {
    private lateinit var file: Path
    private lateinit var repo: LibraryRepository
    private val day = LocalDate.parse("2026-10-01")

    @BeforeTest
    fun freshDatabase() {                                     // every test starts from an empty file
        file = Files.createTempFile("library-test", ".db")
        repo = LibraryRepository(file.toString()).apply { createSchema() }
    }

    @AfterTest
    fun deleteDatabase() { Files.deleteIfExists(file) }

    @Test
    fun `lending a book that is already on loan fails and leaves no partial rows`() {
        val book = repo.addBook("Dune", 1965)
        val ada = repo.addMember("Ada", "ada@example.com")
        val alan = repo.addMember("Alan", "alan@example.com")
        assertIs<LendResult.Lent>(repo.lend(book, ada, day))
        assertEquals(LendResult.AlreadyOnLoan, repo.lend(book, alan, day))
        assertEquals(1, repo.loanCount())
    }

    @Test
    fun `unknown book or member is reported, not thrown`() {
        val ada = repo.addMember("Ada", "ada@example.com")
        val book = repo.addBook("Dune", 1965)
        assertEquals(LendResult.NoSuchBook, repo.lend(99, ada, day))
        assertEquals(LendResult.NoSuchMember, repo.lend(book, 99, day))
        assertEquals(0, repo.loanCount())
    }

    @Test
    fun `returning a book makes it available again`() {
        val book = repo.addBook("Dune", 1965)
        val ada = repo.addMember("Ada", "ada@example.com")
        repo.lend(book, ada, day)
        assertTrue(repo.returnBook(book, day.plusDays(3)))
        assertFalse(repo.returnBook(book, day.plusDays(4)))          // nothing left to return
        assertIs<LendResult.Lent>(repo.lend(book, ada, day.plusDays(5)))
    }

    @Test
    fun `overdue means open and older than 14 days`() {
        val ada = repo.addMember("Ada", "ada@example.com")
        val old = repo.addBook("Old", 2000)
        val recent = repo.addBook("Recent", 2001)
        val returned = repo.addBook("Returned", 2002)
        repo.lend(old, ada, LocalDate.parse("2026-09-01"))
        repo.lend(recent, ada, LocalDate.parse("2026-10-15"))
        repo.lend(returned, ada, LocalDate.parse("2026-09-01"))
        repo.returnBook(returned, LocalDate.parse("2026-09-10"))
        assertEquals(listOf(Overdue("Old", "Ada", LocalDate.parse("2026-09-01"))),
                     repo.overdueLoans(LocalDate.parse("2026-10-21")))
    }

    @Test
    fun `top borrowers are ordered by count, then name`() {
        val (a, b, c) = listOf("Cy", "Bea", "Al").map { repo.addMember(it, "$it@example.com") }
        val books = (1..5).map { repo.addBook("Book $it", 2000 + it) }
        repo.lend(books[0], a, day); repo.lend(books[1], a, day)          // Cy: 2
        repo.lend(books[2], b, day); repo.lend(books[3], b, day)          // Bea: 2
        repo.lend(books[4], c, day)                                       // Al: 1
        assertEquals(listOf(Borrower("Bea", 2), Borrower("Cy", 2)), repo.topBorrowers(2))
    }

    @Test
    fun `an SQL injection attempt is stored as plain text`() {
        val evil = "Robert'); DROP TABLE members; --"
        val id = repo.addMember(evil, "bobby@example.com")
        assertEquals(evil, repo.memberName(id))
        assertEquals("Ada", repo.memberName(repo.addMember("Ada", "ada@example.com")))   // table still there
    }

    @Test
    fun `the database itself refuses a second open loan`() {
        val book = repo.addBook("Dune", 1965)
        val ada = repo.addMember("Ada", "ada@example.com")
        repo.lend(book, ada, day)
        val error = assertFailsWith<SQLException> {                       // bypass the repository's check
            DriverManager.getConnection("jdbc:sqlite:$file").use { c ->
                c.createStatement().use { it.executeUpdate("INSERT INTO loans (book_id, member_id, loaned) VALUES ($book, $ada, '2026-10-02')") }
            }
        }
        assertTrue("UNIQUE" in error.message!!)
    }

    @Test
    fun `eight concurrent lenders, exactly one wins`() {
        val book = repo.addBook("Dune", 1965)
        val members = (1..8).map { repo.addMember("M$it", "m$it@example.com") }
        val gate = CountDownLatch(1)
        val results = Executors.newFixedThreadPool(8).use { pool ->
            val futures = members.map { m -> pool.submit<LendResult> { gate.await(); repo.lend(book, m, day) } }
            gate.countDown()
            futures.map { it.get() }
        }
        assertEquals(1, results.count { it is LendResult.Lent })
        assertEquals(7, results.count { it == LendResult.AlreadyOnLoan })
    }
}
