import java.time.LocalDate
import java.time.temporal.ChronoUnit

// VERSION A: nine tiny functions. Each is "clean"; together you must read all nine.
fun loanDays() = 14L
fun dueDate(loaned: LocalDate) = loaned.plusDays(loanDays())
fun isAfter(a: LocalDate, b: LocalDate) = a.isAfter(b)
fun daysBetween(a: LocalDate, b: LocalDate) = ChronoUnit.DAYS.between(a, b)
fun isLate(loaned: LocalDate, today: LocalDate) = isAfter(today, dueDate(loaned))
fun lateDays(loaned: LocalDate, today: LocalDate) = daysBetween(dueDate(loaned), today)
fun plural(n: Long) = if (n == 1L) "" else "s"
fun lateLabel(n: Long) = "$n day${plural(n)} late"
fun statusA(loaned: LocalDate, today: LocalDate) = if (isLate(loaned, today)) lateLabel(lateDays(loaned, today)) else "on time"

// VERSION B: three functions, each hiding a real decision.
const val LOAN_DAYS = 14L

/** Days past the due date; 0 if not yet due. */
fun daysOverdue(loaned: LocalDate, today: LocalDate): Long =
    maxOf(0, ChronoUnit.DAYS.between(loaned.plusDays(LOAN_DAYS), today))

fun statusB(loaned: LocalDate, today: LocalDate): String = when (val n = daysOverdue(loaned, today)) {
    0L -> "on time"
    1L -> "1 day late"
    else -> "$n days late"
}

fun main() {
    val today = LocalDate.of(2026, 10, 21)
    for (loaned in listOf(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 6), LocalDate.of(2026, 9, 30))) {
        println("$loaned  A: ${statusA(loaned, today)}  B: ${statusB(loaned, today)}")
    }
}
