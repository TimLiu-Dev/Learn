package shelf.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shelf.domain.Book;
import shelf.domain.Loan;
import shelf.domain.Member;
import shelf.store.LoanStore.TryLend;

// Contract tests (L38): one suite, run against every implementation, so the fakes can't drift from SQLite.
abstract class StoreContract {
    protected record Stores(BookStore books, MemberStore members, LoanStore loans) {}

    protected abstract Stores newStores();

    private BookStore books;
    private MemberStore members;
    private LoanStore loans;
    private final LocalDate day1 = LocalDate.of(2026, 10, 1);
    private final LocalDate day2 = LocalDate.of(2026, 10, 2);

    @BeforeEach
    void setUp() {
        Stores s = newStores();
        books = s.books();
        members = s.members();
        loans = s.loans();
    }

    @Test
    void booksRoundTripIncludingAMissingYear() {
        Book dune = books.add("Dune", "Frank Herbert", 1965);
        Book beowulf = books.add("Beowulf", "Anonymous", null);
        assertEquals(Optional.of(dune), books.find(dune.id()));
        assertNull(books.find(beowulf.id()).orElseThrow().year());
        assertTrue(books.find(999).isEmpty());
    }

    @Test
    void listIsOrderedByIdPagedAndFilteredByAuthor() {
        List<Long> ids = List.of(books.add("A", "Le Guin", null).id(), books.add("B", "Herbert", null).id(),
                                 books.add("C", "Le Guin", null).id(), books.add("D", "Le Guin", null).id());
        assertEquals(List.of("B", "C"), books.list(2, 1, null).stream().map(Book::title).toList());
        assertEquals(List.of("C", "D"), books.list(10, 1, "Le Guin").stream().map(Book::title).toList());
        assertEquals(4, books.count(null));
        assertEquals(3, books.count("Le Guin"));
        assertEquals(ids.stream().sorted().toList(), books.list(10, 0, null).stream().map(Book::id).toList());
    }

    @Test
    void updateAndDeleteReportMissingBooks() {
        long id = books.add("Dnue", "Herbert", 1965).id();
        assertEquals("Dune", books.update(id, "Dune", "Frank Herbert", 1965).orElseThrow().title());
        assertEquals("Frank Herbert", books.find(id).orElseThrow().author());
        assertTrue(books.update(999, "X", "Y", null).isEmpty());
        assertTrue(books.delete(id));
        assertFalse(books.delete(id));
    }

    @Test
    void membersAreFoundByIdAndByEmail() {
        Member ada = members.add("Ada", "ada@example.com");
        assertEquals(Optional.of(ada), members.find(ada.id()));
        assertEquals(Optional.of(ada), members.findByEmail("ada@example.com"));
        assertTrue(members.findByEmail("nobody@example.com").isEmpty());
    }

    @Test
    void aClosedLoanIsNoLongerOpenAndClosesOnlyOnce() {
        long book = books.add("Dune", "Herbert", 1965).id();
        long ada = members.add("Ada", "ada@example.com").id();
        Loan loan = loans.add(book, ada, day1);
        assertEquals(Optional.of(loan), loans.openForBook(book));
        assertTrue(loans.close(loan.id(), day2));
        assertTrue(loans.openForBook(book).isEmpty());
        assertEquals(day2, loans.find(loan.id()).orElseThrow().returned());
        assertFalse(loans.close(loan.id(), day2));
    }

    @Test
    void aMembersLoansComeOldestFirstAndAnyLoanMarksTheBook() {
        long b1 = books.add("Dune", "Herbert", 1965).id();
        long b2 = books.add("Emma", "Austen", 1815).id();
        long ada = members.add("Ada", "ada@example.com").id();
        Loan first = loans.add(b1, ada, day1);
        loans.close(first.id(), day2);
        Loan second = loans.add(b2, ada, day2);
        assertEquals(List.of(first.id(), second.id()), loans.forMember(ada).stream().map(Loan::id).toList());
        assertTrue(loans.anyForBook(b1));
        assertFalse(loans.anyForBook(999));
    }

    @Test
    void tryLendRefusesABookThatIsOutAndAMemberAtTheLimit() {
        long b1 = books.add("Dune", "Herbert", 1965).id();
        long b2 = books.add("Emma", "Austen", 1815).id();
        long ada = members.add("Ada", "ada@example.com").id();
        long bob = members.add("Bob", "bob@example.com").id();
        assertTrue(loans.tryLend(b1, ada, day1, 1) instanceof TryLend.Lent);
        assertEquals(new TryLend.BookTaken(), loans.tryLend(b1, bob, day1, 1));
        assertEquals(new TryLend.AtLimit(), loans.tryLend(b2, ada, day1, 1));
    }

    // L46: v1 checked in the service and inserted later, so a burst of requests could break both rules.
    @Test
    void tryLendKeepsBothRulesUnderABurstOfConcurrentCalls() throws Exception {
        long book = books.add("Dune", "Herbert", 1965).id();
        List<Long> crowd = new ArrayList<>();
        for (int i = 1; i <= 20; i++) crowd.add(members.add("M" + i, "m" + i + "@example.com").id());
        List<TryLend> sameBook = burst(20, i -> loans.tryLend(book, crowd.get(i), day1, 3));
        assertEquals(1, sameBook.stream().filter(r -> r instanceof TryLend.Lent).count());

        long greedy = members.add("Greedy", "greedy@example.com").id();
        List<Long> shelf = new ArrayList<>();
        for (int i = 1; i <= 10; i++) shelf.add(books.add("Book " + i, "Anon", null).id());
        List<TryLend> oneMember = burst(10, i -> loans.tryLend(shelf.get(i), greedy, day1, 3));
        assertEquals(3, oneMember.stream().filter(r -> r instanceof TryLend.Lent).count());
        assertEquals(3, loans.forMember(greedy).stream().filter(Loan::isOpen).count());
    }

    @Test
    void tryAddLetsExactlyOneOfTenSimultaneousSignUpsWithOneEmailThrough() throws Exception {
        List<Optional<Member>> results = burst(10, i -> members.tryAdd("Ada", "ada@example.com"));
        assertEquals(1, results.stream().filter(Optional::isPresent).count());
        assertEquals(results.stream().flatMap(Optional::stream).findFirst(), members.findByEmail("ada@example.com"));
    }

    private static <T> List<T> burst(int n, IntFunction<T> call) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<T>> futures = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int k = i;
                futures.add(pool.submit(() -> { start.await(); return call.apply(k); }));
            }
            start.countDown();
            List<T> out = new ArrayList<>();
            for (Future<T> f : futures) out.add(f.get());
            return out;
        }
    }
}
