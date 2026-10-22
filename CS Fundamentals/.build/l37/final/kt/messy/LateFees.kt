package latefees

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Loan(val title: String, val kind: String, val loaned: LocalDate, val returned: LocalDate?)

// Works out late fees for a member's loans.
// Fees are capped at $10 per loan.
fun calculateFees(loans: List<Loan>, today: LocalDate, premium: Boolean, detailed: Boolean): String {
    var total = 0
    var out = ""
    if (detailed) {
        out = out + "Late fees as of " + today + "\n"
    }
    for (l in loans) {
        var fee = 0
        if (l.returned != null) {
            // returned: count days from due date to return date
            var due = l.loaned.plusDays(14)
            if (due.dayOfWeek == DayOfWeek.SUNDAY) {
                due = due.plusDays(1)
            }
            val days = ChronoUnit.DAYS.between(due, l.returned).toInt()
            if (days > 0) {
                if (l.kind == "dvd") {
                    fee = days * 100
                } else {
                    if (l.kind == "new") {
                        fee = days * 50
                    } else {
                        fee = days * 25
                    }
                }
            }
        } else {
            // still out: count days from due date to today
            var due = l.loaned.plusDays(14)
            if (due.dayOfWeek == DayOfWeek.SUNDAY) {
                due = due.plusDays(1)
            }
            val days = ChronoUnit.DAYS.between(due, today).toInt()
            if (days > 0) {
                if (l.kind == "dvd") {
                    fee = days * 100
                } else if (l.kind == "new") {
                    fee = days * 50
                } else {
                    fee = days * 25
                }
                fee = fee + 200
            }
        }
        if (premium) {
            if (fee > 0) {
                fee = fee - 100
                if (fee < 0) {
                    fee = 0
                }
            }
        }
        if (fee > 1500) {
            fee = 1500
        }
        total = total + fee
        if (detailed) {
            if (fee > 0) {
                out = out + l.title + ": $" + fee / 100 + "." + (if (fee % 100 < 10) "0" else "") + fee % 100 + "\n"
            } else {
                out = out + l.title + ": no fee\n"
            }
        }
    }
    if (detailed) {
        out = out + "TOTAL: $" + total / 100 + "." + (if (total % 100 < 10) "0" else "") + total % 100
        return out
    }
    return "$" + total / 100 + "." + (if (total % 100 < 10) "0" else "") + total % 100
}
