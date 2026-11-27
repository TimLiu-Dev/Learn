import java.time.LocalDate

data class Loan(val copy: String, val loaned: LocalDate, val returned: LocalDate? = null)

class Borrower(val memberId: Long) {
    val loans = mutableListOf<Loan>()              // anyone can edit this list...
    val openLoans: List<Loan> get() = loans        // ...and this hands it out, too

    fun lend(copy: String, today: LocalDate): String {
        loans += Loan(copy, today)                 // no rules at all yet
        return "Lent $copy"
    }

    fun giveBack(copy: String, today: LocalDate): Boolean = TODO("close the open loan of this copy")
}
