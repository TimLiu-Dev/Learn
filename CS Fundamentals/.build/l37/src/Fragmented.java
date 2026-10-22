import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Fragmented {
    // VERSION A: nine tiny methods. Each is "clean"; together you must read all nine.
    static long loanDays() { return 14; }
    static LocalDate dueDate(LocalDate loaned) { return loaned.plusDays(loanDays()); }
    static boolean isAfter(LocalDate a, LocalDate b) { return a.isAfter(b); }
    static long daysBetween(LocalDate a, LocalDate b) { return ChronoUnit.DAYS.between(a, b); }
    static boolean isLate(LocalDate loaned, LocalDate today) { return isAfter(today, dueDate(loaned)); }
    static long lateDays(LocalDate loaned, LocalDate today) { return daysBetween(dueDate(loaned), today); }
    static String plural(long n) { return n == 1 ? "" : "s"; }
    static String lateLabel(long n) { return n + " day" + plural(n) + " late"; }
    static String statusA(LocalDate loaned, LocalDate today) {
        return isLate(loaned, today) ? lateLabel(lateDays(loaned, today)) : "on time";
    }

    // VERSION B: three methods, each hiding a real decision.
    static final long LOAN_DAYS = 14;

    /** Days past the due date; 0 if not yet due. */
    static long daysOverdue(LocalDate loaned, LocalDate today) {
        return Math.max(0, ChronoUnit.DAYS.between(loaned.plusDays(LOAN_DAYS), today));
    }

    static String statusB(LocalDate loaned, LocalDate today) {
        long n = daysOverdue(loaned, today);
        return switch ((int) Math.min(n, 2)) {
            case 0 -> "on time";
            case 1 -> "1 day late";
            default -> n + " days late";
        };
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 10, 21);
        for (LocalDate loaned : List.of(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 6), LocalDate.of(2026, 9, 30))) {
            System.out.println(loaned + "  A: " + statusA(loaned, today) + "  B: " + statusB(loaned, today));
        }
    }
}
