import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregate {
    record CopyId(String barcode) {}                          // a value object
    record Loan(CopyId copy, LocalDate loaned) {}             // another one

    // The aggregate root. It carries the version it was loaded at, like L36's version column.
    static final class Borrower {
        final long memberId;
        final int version;
        private final List<Loan> loans;

        Borrower(long memberId, int version, List<Loan> loans) {
            this.memberId = memberId;
            this.version = version;
            this.loans = new ArrayList<>(loans);
        }

        List<Loan> openLoans() { return List.copyOf(loans); }

        String lend(CopyId copy, LocalDate today) {
            if (loans.size() >= 3) return "Blocked: already has 3 loans";
            loans.add(new Loan(copy, today));
            return "Lent " + copy.barcode();
        }
    }

    static final class ConcurrentUpdate extends Exception {
        ConcurrentUpdate(String message) { super(message); }
    }

    record Row(int version, List<Loan> loans) {}

    // Saves a whole aggregate at once, and only if nobody saved it since it was loaded.
    static final class BorrowerRepository {
        private final Map<Long, Row> rows = new HashMap<>();

        Borrower load(long id) {
            Row row = rows.getOrDefault(id, new Row(0, List.of()));
            return new Borrower(id, row.version(), row.loans());
        }

        void save(Borrower b) throws ConcurrentUpdate {
            int current = rows.containsKey(b.memberId) ? rows.get(b.memberId).version() : 0;
            if (current != b.version) {
                throw new ConcurrentUpdate("member " + b.memberId + " is at version " + current + "; you loaded " + b.version);
            }
            rows.put(b.memberId, new Row(current + 1, b.openLoans()));
        }
    }

    public static void main(String[] args) throws ConcurrentUpdate {
        System.out.println("same value, same object? " + new CopyId("C-1").equals(new CopyId("C-1"))
                + ", " + (new CopyId("C-1") == new CopyId("C-1")));
        BorrowerRepository repo = new BorrowerRepository();
        LocalDate today = LocalDate.of(2026, 11, 26);
        Borrower ada = repo.load(1);
        ada.lend(new CopyId("C-1"), today);
        ada.lend(new CopyId("C-2"), today);
        repo.save(ada);

        // Two desks load Ada's account at the same moment. Each lends her a third book.
        Borrower desk1 = repo.load(1);
        Borrower desk2 = repo.load(1);
        System.out.println("desk 1: " + desk1.lend(new CopyId("C-3"), today));
        repo.save(desk1);
        System.out.println("desk 2: " + desk2.lend(new CopyId("C-4"), today));
        try {
            repo.save(desk2);
        } catch (ConcurrentUpdate e) {
            System.out.println("desk 2 save refused: " + e.getMessage());
            System.out.println("desk 2 reloads and retries: " + repo.load(1).lend(new CopyId("C-4"), today));
        }
        System.out.println("Ada's loans: " + repo.load(1).openLoans().stream().map(l -> l.copy().barcode()).toList());
    }
}
