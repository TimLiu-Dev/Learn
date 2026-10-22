package library;

import library.LibraryRepository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class LibraryRepositoryTest {
    private Path file;
    private LibraryRepository repo;
    private final LocalDate day = LocalDate.parse("2026-10-01");

    @BeforeEach
    void freshDatabase() throws Exception {                   // every test starts from an empty file
        file = Files.createTempFile("library-test", ".db");
        repo = new LibraryRepository(file.toString());
        repo.createSchema();
    }

    @AfterEach
    void deleteDatabase() throws Exception { Files.deleteIfExists(file); }

    @Test
    void lendingABookAlreadyOnLoanFailsAndLeavesNoPartialRows() throws SQLException {
        long book = repo.addBook("Dune", 1965);
        long ada = repo.addMember("Ada", "ada@example.com");
        long alan = repo.addMember("Alan", "alan@example.com");
        assertInstanceOf(Lent.class, repo.lend(book, ada, day));
        assertEquals(new AlreadyOnLoan(), repo.lend(book, alan, day));
        assertEquals(1, repo.loanCount());
    }

    @Test
    void unknownBookOrMemberIsReportedNotThrown() throws SQLException {
        long ada = repo.addMember("Ada", "ada@example.com");
        long book = repo.addBook("Dune", 1965);
        assertEquals(new NoSuchBook(), repo.lend(99, ada, day));
        assertEquals(new NoSuchMember(), repo.lend(book, 99, day));
        assertEquals(0, repo.loanCount());
    }

    @Test
    void returningABookMakesItAvailableAgain() throws SQLException {
        long book = repo.addBook("Dune", 1965);
        long ada = repo.addMember("Ada", "ada@example.com");
        repo.lend(book, ada, day);
        assertTrue(repo.returnBook(book, day.plusDays(3)));
        assertFalse(repo.returnBook(book, day.plusDays(4)));        // nothing left to return
        assertInstanceOf(Lent.class, repo.lend(book, ada, day.plusDays(5)));
    }

    @Test
    void overdueMeansOpenAndOlderThan14Days() throws SQLException {
        long ada = repo.addMember("Ada", "ada@example.com");
        long old = repo.addBook("Old", 2000);
        long recent = repo.addBook("Recent", 2001);
        long returned = repo.addBook("Returned", 2002);
        repo.lend(old, ada, LocalDate.parse("2026-09-01"));
        repo.lend(recent, ada, LocalDate.parse("2026-10-15"));
        repo.lend(returned, ada, LocalDate.parse("2026-09-01"));
        repo.returnBook(returned, LocalDate.parse("2026-09-10"));
        assertEquals(List.of(new Overdue("Old", "Ada", LocalDate.parse("2026-09-01"))),
                     repo.overdueLoans(LocalDate.parse("2026-10-21")));
    }

    @Test
    void topBorrowersAreOrderedByCountThenName() throws SQLException {
        long cy = repo.addMember("Cy", "cy@example.com");
        long bea = repo.addMember("Bea", "bea@example.com");
        long al = repo.addMember("Al", "al@example.com");
        List<Long> books = new ArrayList<>();
        for (int i = 1; i <= 5; i++) books.add(repo.addBook("Book " + i, 2000 + i));
        repo.lend(books.get(0), cy, day); repo.lend(books.get(1), cy, day);      // Cy: 2
        repo.lend(books.get(2), bea, day); repo.lend(books.get(3), bea, day);    // Bea: 2
        repo.lend(books.get(4), al, day);                                        // Al: 1
        assertEquals(List.of(new Borrower("Bea", 2), new Borrower("Cy", 2)), repo.topBorrowers(2));
    }

    @Test
    void anSqlInjectionAttemptIsStoredAsPlainText() throws SQLException {
        String evil = "Robert'); DROP TABLE members; --";
        long id = repo.addMember(evil, "bobby@example.com");
        assertEquals(evil, repo.memberName(id));
        assertEquals("Ada", repo.memberName(repo.addMember("Ada", "ada@example.com")));   // table still there
    }

    @Test
    void theDatabaseItselfRefusesASecondOpenLoan() throws SQLException {
        long book = repo.addBook("Dune", 1965);
        long ada = repo.addMember("Ada", "ada@example.com");
        repo.lend(book, ada, day);
        SQLException error = assertThrows(SQLException.class, () -> {   // bypass the repository's check
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + file); Statement st = c.createStatement()) {
                st.executeUpdate("INSERT INTO loans (book_id, member_id, loaned) VALUES (" + book + ", " + ada + ", '2026-10-02')");
            }
        });
        assertTrue(error.getMessage().contains("UNIQUE"));
    }

    @Test
    void eightConcurrentLendersExactlyOneWins() throws Exception {
        long book = repo.addBook("Dune", 1965);
        List<Long> members = new ArrayList<>();
        for (int i = 1; i <= 8; i++) members.add(repo.addMember("M" + i, "m" + i + "@example.com"));
        CountDownLatch gate = new CountDownLatch(1);
        List<LendResult> results = new ArrayList<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            List<Future<LendResult>> futures = new ArrayList<>();
            for (long m : members) futures.add(pool.submit(() -> { gate.await(); return repo.lend(book, m, day); }));
            gate.countDown();
            for (Future<LendResult> f : futures) results.add(f.get());
        }
        assertEquals(1, results.stream().filter(r -> r instanceof Lent).count());
        assertEquals(7, results.stream().filter(r -> r.equals(new AlreadyOnLoan())).count());
    }
}
