import java.time.LocalDate

data class Loan(val copy: String, val loaned: LocalDate, val returned: LocalDate? = null)

class Borrower(val memberId: Long) {
    private val loans = mutableListOf<Loan>()
    // The mistake: private, but handed out anyway. The read-only type doesn't stop a cast.
    val openLoans: List<Loan> get() = loans

    fun lend(copy: String, today: LocalDate): String {
        val open = loans.filter { it.returned == null }
        val blocked = when {
            open.any { it.loaned.plusDays(14) < today } -> "has an overdue loan"
            open.any { it.copy == copy } -> "already has $copy"
            open.size >= 3 -> "already has 3 loans"
            else -> null
        }
        if (blocked != null) return "Blocked: $blocked"
        loans += Loan(copy, today)
        return "Lent $copy"
    }

    fun giveBack(copy: String, today: LocalDate): Boolean {
        val i = loans.indexOfFirst { it.copy == copy && it.returned == null }
        if (i < 0) return false
        loans.removeAt(i)
        return true
    }
}
