package latefees;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import latefees.LateFees.Loan;

/**
 * The library's late-fee rules for one member.
 *
 * <p>Callers need two things: {@link #assess} and the {@link Assessment} it returns. Due dates,
 * rates, surcharges, the premium discount and the cap are all decided in here.
 */
public final class LateFeePolicy {
    private static final long LOAN_DAYS = 14;
    private static final int STILL_OUT_SURCHARGE_CENTS = 200;
    private static final int PREMIUM_DISCOUNT_CENTS = 100;
    private static final int MAX_FEE_PER_LOAN_CENTS = 1500;   // $15. The old comment said $10; the code always charged up to $15.

    public record Fee(String title, int cents) {}

    /** The fees for one member on one day. */
    public record Assessment(LocalDate asOf, List<Fee> fees) {
        public int totalCents() {
            return fees.stream().mapToInt(Fee::cents).sum();
        }

        public String report() {
            StringBuilder out = new StringBuilder("Late fees as of " + asOf + "\n");
            for (Fee fee : fees) {
                out.append(fee.title()).append(": ")
                   .append(fee.cents() > 0 ? formatCents(fee.cents()) : "no fee").append("\n");
            }
            return out.append("TOTAL: ").append(formatCents(totalCents())).toString();
        }
    }

    private final boolean isPremium;

    public LateFeePolicy(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public Assessment assess(List<Loan> loans, LocalDate today) {
        return new Assessment(today, loans.stream().map(loan -> new Fee(loan.title(), feeCents(loan, today))).toList());
    }

    private int feeCents(Loan loan, LocalDate today) {
        LocalDate end = loan.returned() != null ? loan.returned() : today;
        int daysLate = (int) ChronoUnit.DAYS.between(dueDate(loan.loaned()), end);
        if (daysLate <= 0) return 0;
        int fee = daysLate * dailyRateCents(loan.kind());
        if (loan.returned() == null) fee += STILL_OUT_SURCHARGE_CENTS;
        if (isPremium) fee = Math.max(0, fee - PREMIUM_DISCOUNT_CENTS);
        return Math.min(fee, MAX_FEE_PER_LOAN_CENTS);
    }

    // The library is closed on Sundays, so a Sunday due date moves to Monday.
    private static LocalDate dueDate(LocalDate loaned) {
        LocalDate due = loaned.plusDays(LOAN_DAYS);
        return due.getDayOfWeek() == DayOfWeek.SUNDAY ? due.plusDays(1) : due;
    }

    private static int dailyRateCents(String kind) {
        return switch (kind) {
            case "dvd" -> 100;
            case "new" -> 50;
            default -> 25;          // "standard" and anything unrecognised
        };
    }

    static String formatCents(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}
