import java.time.LocalDate;
import java.util.*;

public class Practice {
    record Loan(long id, long bookId, long memberId, LocalDate loaned, LocalDate returned) {
        boolean isOpen() { return returned == null; }
    }

    interface LoanStore {
        Loan add(long bookId, long memberId, LocalDate loaned);
        Optional<Loan> find(long id);
        Optional<Loan> openForBook(long bookId);
        List<Loan> forMember(long memberId);                    // oldest first
        boolean anyForBook(long bookId);
        boolean close(long id, LocalDate returned);             // false: missing or already closed
    }

    static class MemoryLoanStore implements LoanStore {
        private final TreeMap<Long, Loan> loans = new TreeMap<>();   // sorted by id, like ORDER BY id
        private long nextId = 1;

        public Loan add(long bookId, long memberId, LocalDate loaned) {
            throw new UnsupportedOperationException("TODO: refuse a second open loan for the book, then store a new Loan");
        }
        public Optional<Loan> find(long id) { throw new UnsupportedOperationException("TODO"); }
        public Optional<Loan> openForBook(long bookId) { throw new UnsupportedOperationException("TODO"); }
        public List<Loan> forMember(long memberId) { throw new UnsupportedOperationException("TODO"); }
        public boolean anyForBook(long bookId) { throw new UnsupportedOperationException("TODO"); }
        public boolean close(long id, LocalDate returned) { throw new UnsupportedOperationException("TODO: only an open loan can be closed"); }
    }

    // CHECKS
    static final LocalDate DAY1 = LocalDate.of(2026, 10, 1), DAY2 = LocalDate.of(2026, 10, 2);

    public static void main(String[] args) {
        check("ids start at 1", new Loan(1, 7, 1, DAY1, null), () -> new MemoryLoanStore().add(7, 1, DAY1));
        check("a loan can be found again, and a missing one can't", List.of(true, false), () -> {
            LoanStore s = new MemoryLoanStore();
            Loan loan = s.add(7, 1, DAY1);
            return List.of(s.find(loan.id()).equals(Optional.of(loan)), s.find(99).isPresent());
        });
        check("close works once, then reports false", List.of(true, false, false, false), () -> {
            LoanStore s = new MemoryLoanStore();
            Loan loan = s.add(7, 1, DAY1);
            return List.of(s.close(loan.id(), DAY2), s.openForBook(7).isPresent(), s.close(loan.id(), DAY2), s.close(99, DAY2));
        });
        check("a second open loan for a book is refused, like the unique index", "IllegalStateException", () -> {
            LoanStore s = new MemoryLoanStore();
            s.add(7, 1, DAY1);
            try { s.add(7, 2, DAY1); return "no exception"; }
            catch (IllegalStateException e) { return "IllegalStateException"; }
        });
        check("a returned book can be lent again", 7L, () -> {
            LoanStore s = new MemoryLoanStore();
            s.close(s.add(7, 1, DAY1).id(), DAY2);
            return s.add(7, 2, DAY2).bookId();
        });
        check("a member's loans come oldest first", List.of(1L, 3L), () -> {
            LoanStore s = new MemoryLoanStore();
            s.add(7, 1, DAY1); s.add(8, 2, DAY1); s.add(9, 1, DAY2);
            s.close(1, DAY2);
            return s.forMember(1).stream().map(Loan::id).toList();
        });
        check("any loan, even a returned one, marks the book", List.of(true, false), () -> {
            LoanStore s = new MemoryLoanStore();
            s.close(s.add(7, 1, DAY1).id(), DAY2);
            return List.of(s.anyForBook(7), s.anyForBook(99));
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
