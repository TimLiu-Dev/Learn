import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class Seams {
    static class LegacyDb {                       // the legacy code's hard-wired database
        static LocalDate dueDate(int loanId) { throw new IllegalStateException("no database in tests"); }
    }

    // 1. Seam: the old signature stays and delegates; the new overload takes the dependencies.
    static int lateFeeCents(int loanId) {
        return lateFeeCents(loanId, LegacyDb::dueDate, LocalDate.now());
    }
    static int lateFeeCents(int loanId, IntFunction<LocalDate> dueDateOf, LocalDate today) {
        long days = afterGrace(ChronoUnit.DAYS.between(dueDateOf.apply(loanId), today));  // 2. one new call
        return (int) Math.min(days * 25, 1000L);
    }

    // 2. Sprout method: the new rule (one free day) lives in a new, pure method, tested on its own.
    static long afterGrace(long daysLate) { return Math.max(daysLate - 1, 0); }

    // 3. Wrap method: the old body of chargeLateFee was renamed chargeUnaudited.
    //    The old name now adds the new behaviour and calls the old body.
    static int chargeUnaudited(int loanId) {
        return lateFeeCents(loanId, id -> LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 9));
    }
    static final List<String> audit = new ArrayList<>();
    static int chargeLateFee(int loanId) {
        int cents = chargeUnaudited(loanId);
        audit.add("loan " + loanId + " charged " + cents);
        return cents;
    }

    public static void main(String[] args) {
        try { lateFeeCents(7); } catch (IllegalStateException e) { System.out.println(e); }
        System.out.println(lateFeeCents(7, id -> LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 9)));
        System.out.println(afterGrace(1) + " " + afterGrace(8));
        chargeLateFee(7);
        System.out.println(audit);
    }
}
