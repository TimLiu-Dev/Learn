data class Loan(val title: String, val daysLate: Int, val student: Boolean)

// The strategy IS a function type: no interface, no implementing classes
typealias LateFeePolicy = (Loan) -> Int

val flatFee: LateFeePolicy = { if (it.daysLate > 0) 500 else 0 }
fun perDay(cents: Int): LateFeePolicy = { it.daysLate * cents }
fun capped(inner: LateFeePolicy, cap: Int): LateFeePolicy = { minOf(inner(it), cap) }

class Library(private val policy: LateFeePolicy) {
    fun totalFees(loans: List<Loan>) = loans.sumOf(policy)
}

fun main() {
    val loans = listOf(Loan("Dune", 3, false), Loan("Emma", 6, true), Loan("Ulysses", 40, false))
    val policies = linkedMapOf(
        "flat" to flatFee,
        "per-day" to perDay(25),
        "capped" to capped(perDay(25), 600),
        "students free" to { loan: Loan -> if (loan.student) 0 else loan.daysLate * 25 },
    )
    for ((name, p) in policies) println("$name: ${Library(p).totalFees(loans)} cents")

    val sorted = loans.sortedByDescending { it.daysLate }      // a comparator built from a lambda
    println("most late first: ${sorted.map { it.title }}")
}
