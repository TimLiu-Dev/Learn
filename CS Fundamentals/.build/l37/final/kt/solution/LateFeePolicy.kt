package latefees

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * The library's late-fee rules for one member.
 *
 * Callers need two things: [assess] and the [Assessment] it returns. Due dates,
 * rates, surcharges, the premium discount and the cap are all decided in here.
 */
class LateFeePolicy(private val isPremium: Boolean) {

    fun assess(loans: List<Loan>, today: LocalDate): Assessment =
        Assessment(today, loans.map { it.title to feeCents(it, today) })

    private fun feeCents(loan: Loan, today: LocalDate): Int {
        val daysLate = ChronoUnit.DAYS.between(dueDate(loan.loaned), loan.returned ?: today).toInt()
        if (daysLate <= 0) return 0
        var fee = daysLate * dailyRateCents(loan.kind)
        if (loan.returned == null) fee += STILL_OUT_SURCHARGE_CENTS
        if (isPremium) fee = maxOf(0, fee - PREMIUM_DISCOUNT_CENTS)
        return minOf(fee, MAX_FEE_PER_LOAN_CENTS)
    }

    private companion object {
        const val LOAN_DAYS = 14L
        const val STILL_OUT_SURCHARGE_CENTS = 200
        const val PREMIUM_DISCOUNT_CENTS = 100
        const val MAX_FEE_PER_LOAN_CENTS = 1500   // $15. The old comment said $10; the code always charged up to $15.

        // The library is closed on Sundays, so a Sunday due date moves to Monday.
        fun dueDate(loaned: LocalDate): LocalDate {
            val due = loaned.plusDays(LOAN_DAYS)
            return if (due.dayOfWeek == DayOfWeek.SUNDAY) due.plusDays(1) else due
        }

        fun dailyRateCents(kind: String): Int = when (kind) {
            "dvd" -> 100
            "new" -> 50
            else -> 25          // "standard" and anything unrecognised
        }
    }
}

/** The fees for one member on one day. */
class Assessment(val asOf: LocalDate, val feesCents: List<Pair<String, Int>>) {
    val totalCents: Int get() = feesCents.sumOf { (_, fee) -> fee }

    fun report(): String = buildString {
        appendLine("Late fees as of $asOf")
        for ((title, fee) in feesCents) appendLine(if (fee > 0) "$title: ${formatCents(fee)}" else "$title: no fee")
        append("TOTAL: ${formatCents(totalCents)}")
    }
}

fun formatCents(cents: Int): String = "$%d.%02d".format(cents / 100, cents % 100)
