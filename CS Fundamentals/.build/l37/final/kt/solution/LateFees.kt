package latefees

import java.time.LocalDate

data class Loan(val title: String, val kind: String, val loaned: LocalDate, val returned: LocalDate?)

/** Kept so existing callers (and the characterisation tests) still work. New code: use [LateFeePolicy]. */
fun calculateFees(loans: List<Loan>, today: LocalDate, premium: Boolean, detailed: Boolean): String {
    val assessment = LateFeePolicy(isPremium = premium).assess(loans, today)
    return if (detailed) assessment.report() else formatCents(assessment.totalCents)
}
