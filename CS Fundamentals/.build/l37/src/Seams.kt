import java.time.LocalDate
import java.time.temporal.ChronoUnit

object LegacyDb {                        // the legacy code's hard-wired database
    fun dueDate(loanId: Int): LocalDate = error("no database in tests")
}

// 1. Seam: the two hard-wired calls became parameters. The defaults keep every old call working.
fun lateFeeCents(
    loanId: Int,
    dueDateOf: (Int) -> LocalDate = LegacyDb::dueDate,
    today: LocalDate = LocalDate.now(),
): Int {
    val days = afterGrace(ChronoUnit.DAYS.between(dueDateOf(loanId), today))  // 2. one new call
    return minOf(days * 25, 1000L).toInt()
}

// 2. Sprout method: the new rule (one free day) lives in a new, pure function, tested on its own.
fun afterGrace(daysLate: Long): Long = maxOf(daysLate - 1, 0)

// 3. Wrap method: the old body of chargeLateFee was renamed chargeUnaudited.
//    The old name now adds the new behaviour and calls the old body.
fun chargeUnaudited(loanId: Int): Int = lateFeeCents(loanId, { LocalDate.of(2026, 3, 1) }, LocalDate.of(2026, 3, 9))
val audit = mutableListOf<String>()
fun chargeLateFee(loanId: Int): Int {
    val cents = chargeUnaudited(loanId)
    audit += "loan $loanId charged $cents"
    return cents
}

fun main() {
    println(runCatching { lateFeeCents(7) }.exceptionOrNull())    // the old call: untestable
    println(lateFeeCents(7, dueDateOf = { LocalDate.of(2026, 3, 1) }, today = LocalDate.of(2026, 3, 9)))
    println("${afterGrace(1)} ${afterGrace(8)}")                    // the sprout, tested alone
    chargeLateFee(7)
    println(audit)
}
