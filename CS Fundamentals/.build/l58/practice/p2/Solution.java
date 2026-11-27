import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Loan(String copy, LocalDate loaned, LocalDate returned) {}

    static final class Borrower {
        final long memberId;
        private final List<Loan> loans = new ArrayList<>();   // only the root can change it

        Borrower(long memberId) { this.memberId = memberId; }

        List<Loan> openLoans() {                              // a new, unmodifiable list every time
            return loans.stream().filter(l -> l.returned() == null).toList();
        }

        String lend(String copy, LocalDate today) {
            List<Loan> open = openLoans();
            String blocked = null;
            if (open.stream().anyMatch(l -> l.loaned().plusDays(14).isBefore(today))) blocked = "has an overdue loan";
            else if (open.stream().anyMatch(l -> l.copy().equals(copy))) blocked = "already has " + copy;
            else if (open.size() >= 3) blocked = "already has 3 loans";
            if (blocked != null) return "Blocked: " + blocked;
            loans.add(new Loan(copy, today, null));
            return "Lent " + copy;
        }

        boolean giveBack(String copy, LocalDate today) {
            for (int i = 0; i < loans.size(); i++) {
                Loan l = loans.get(i);
                if (l.copy().equals(copy) && l.returned() == null) {
                    loans.set(i, new Loan(l.copy(), l.loaned(), today));   // replace the value, don't mutate it
                    return true;
                }
            }
            return false;
        }
    }

    // CHECKS
    static final LocalDate DAY = LocalDate.of(2026, 11, 2);

    public static void main(String[] args) {
        check("three loans are fine, a fourth is blocked", "Lent C-1 | Blocked: already has 3 loans | 3", () -> {
            Borrower b = new Borrower(1);
            String first = b.lend("C-1", DAY);
            b.lend("C-2", DAY);
            b.lend("C-3", DAY);
            return first + " | " + b.lend("C-4", DAY) + " | " + b.openLoans().size();
        });
        check("an overdue loan blocks the next one", "Lent C-2 | Blocked: has an overdue loan", () -> {
            Borrower b = new Borrower(1);
            b.lend("C-1", DAY);
            return b.lend("C-2", DAY.plusDays(14)) + " | " + b.lend("C-3", DAY.plusDays(15));
        });
        check("the same copy can't be borrowed twice", "Blocked: already has C-1", () -> {
            Borrower b = new Borrower(1);
            b.lend("C-1", DAY);
            return b.lend("C-1", DAY);
        });
        check("giving a copy back frees a slot", "true false Lent C-4 [C-1, C-3, C-4]", () -> {
            Borrower b = new Borrower(1);
            for (String c : List.of("C-1", "C-2", "C-3")) b.lend(c, DAY);
            boolean back = b.giveBack("C-2", DAY.plusDays(3));
            boolean again = b.giveBack("C-2", DAY.plusDays(4));
            String lent = b.lend("C-4", DAY.plusDays(4));
            return back + " " + again + " " + lent + " " + b.openLoans().stream().map(Loan::copy).toList();
        });
        check("editing the list it hands out changes nothing inside", "[C-1]", () -> {
            Borrower b = new Borrower(1);
            b.lend("C-1", DAY);
            try { b.openLoans().add(new Loan("C-9", DAY, null)); } catch (UnsupportedOperationException ignored) { }
            return b.openLoans().stream().map(Loan::copy).toList().toString();
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
