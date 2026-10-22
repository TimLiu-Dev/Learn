package latefees;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class LateFees {
    public record Loan(String title, String kind, LocalDate loaned, LocalDate returned) {}

    // Works out late fees for a member's loans.
    // Fees are capped at $10 per loan.
    public static String calculateFees(List<Loan> loans, LocalDate today, boolean premium, boolean detailed) {
        int total = 0;
        String out = "";
        if (detailed) {
            out = out + "Late fees as of " + today + "\n";
        }
        for (Loan l : loans) {
            int fee = 0;
            if (l.returned() != null) {
                // returned: count days from due date to return date
                LocalDate due = l.loaned().plusDays(14);
                if (due.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    due = due.plusDays(1);
                }
                int days = (int) ChronoUnit.DAYS.between(due, l.returned());
                if (days > 0) {
                    if (l.kind().equals("dvd")) {
                        fee = days * 100;
                    } else {
                        if (l.kind().equals("new")) {
                            fee = days * 50;
                        } else {
                            fee = days * 25;
                        }
                    }
                }
            } else {
                // still out: count days from due date to today
                LocalDate due = l.loaned().plusDays(14);
                if (due.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    due = due.plusDays(1);
                }
                int days = (int) ChronoUnit.DAYS.between(due, today);
                if (days > 0) {
                    if (l.kind().equals("dvd")) {
                        fee = days * 100;
                    } else if (l.kind().equals("new")) {
                        fee = days * 50;
                    } else {
                        fee = days * 25;
                    }
                    fee = fee + 200;
                }
            }
            if (premium) {
                if (fee > 0) {
                    fee = fee - 100;
                    if (fee < 0) {
                        fee = 0;
                    }
                }
            }
            if (fee > 1500) {
                fee = 1500;
            }
            total = total + fee;
            if (detailed) {
                if (fee > 0) {
                    out = out + l.title() + ": $" + fee / 100 + "." + (fee % 100 < 10 ? "0" : "") + fee % 100 + "\n";
                } else {
                    out = out + l.title() + ": no fee\n";
                }
            }
        }
        if (detailed) {
            out = out + "TOTAL: $" + total / 100 + "." + (total % 100 < 10 ? "0" : "") + total % 100;
            return out;
        }
        return "$" + total / 100 + "." + (total % 100 < 10 ? "0" : "") + total % 100;
    }
}
