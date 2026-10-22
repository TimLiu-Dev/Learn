import java.time.LocalDate;
import java.util.List;

public class Isp {
    record Overdue(String title, String member, LocalDate loaned) {}

    // ---------- Before: one fat interface for everything the library does ----------
    interface LibraryService {
        long addBook(String title, int year);
        void removeBook(long id);
        boolean lend(long bookId, long memberId);
        boolean returnBook(long bookId);
        List<Overdue> overdueLoans(LocalDate today);
        List<String> topBorrowers(int limit);
        String exportCsv();
    }

    static final class OverdueEmailerBefore {             // uses ONE of the seven methods
        private final LibraryService library;
        OverdueEmailerBefore(LibraryService library) { this.library = library; }
        List<String> drafts(LocalDate today) {
            return library.overdueLoans(today).stream().map(o -> "To " + o.member() + ": please return " + o.title()).toList();
        }
    }

    // To test it, a fake must still implement all seven.
    static final class FatFake implements LibraryService {
        public List<Overdue> overdueLoans(LocalDate today) { return List.of(new Overdue("Dune", "Ada", LocalDate.parse("2026-10-01"))); }
        public long addBook(String title, int year) { throw new UnsupportedOperationException("not needed"); }
        public void removeBook(long id) { throw new UnsupportedOperationException("not needed"); }
        public boolean lend(long bookId, long memberId) { throw new UnsupportedOperationException("not needed"); }
        public boolean returnBook(long bookId) { throw new UnsupportedOperationException("not needed"); }
        public List<String> topBorrowers(int limit) { throw new UnsupportedOperationException("not needed"); }
        public String exportCsv() { throw new UnsupportedOperationException("not needed"); }
    }

    // ---------- After: small role interfaces; clients depend on the role they use ----------
    interface BookCatalog { long addBook(String title, int year); void removeBook(long id); }
    interface Lending { boolean lend(long bookId, long memberId); boolean returnBook(long bookId); }
    @FunctionalInterface interface OverdueReport { List<Overdue> overdueLoans(LocalDate today); }

    static final class OverdueEmailer {
        private final OverdueReport report;
        OverdueEmailer(OverdueReport report) { this.report = report; }
        List<String> drafts(LocalDate today) {
            return report.overdueLoans(today).stream().map(o -> "To " + o.member() + ": please return " + o.title()).toList();
        }
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.parse("2026-10-21");
        System.out.println("before: " + new OverdueEmailerBefore(new FatFake()).drafts(today));
        OverdueReport fake = t -> List.of(new Overdue("Dune", "Ada", LocalDate.parse("2026-10-01")));   // the whole fake
        System.out.println("after:  " + new OverdueEmailer(fake).drafts(today));
    }
}
