package latefees;

import java.time.LocalDate;
import java.util.List;

public final class LateFees {
    public record Loan(String title, String kind, LocalDate loaned, LocalDate returned) {}

    /** Kept so existing callers (and the characterisation tests) still work. New code: use {@link LateFeePolicy}. */
    public static String calculateFees(List<Loan> loans, LocalDate today, boolean premium, boolean detailed) {
        LateFeePolicy.Assessment assessment = new LateFeePolicy(premium).assess(loans, today);
        return detailed ? assessment.report() : LateFeePolicy.formatCents(assessment.totalCents());
    }
}
