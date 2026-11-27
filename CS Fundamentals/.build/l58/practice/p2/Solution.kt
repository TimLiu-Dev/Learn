import java.time.LocalDate

data class Loan(val copy: String, val loaned: LocalDate, val returned: LocalDate? = null)

class Borrower(val memberId: Long) {
    private val loans = mutableListOf<Loan>()                   // only the root can change it
    val openLoans: List<Loan> get() = loans.filter { it.returned == null }   // a new list every time

    fun lend(copy: String, today: LocalDate): String {
        val open = openLoans
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
        loans[i] = loans[i].copy(returned = today)               // Loan is a value: replace it, don't mutate it
        return true
    }
}
