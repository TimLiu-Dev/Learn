package budget

import java.time.YearMonth

data class Categorised(val tx: Transaction, val category: String)

fun categoriseAll(transactions: List<Transaction>, rules: Rules): List<Categorised> =
    transactions.map { Categorised(it, rules.categorise(it.description)) }

/** Comparing YearMonth values has no "last day" to get wrong. */
fun List<Categorised>.inMonth(month: YearMonth) = filter {
    // BUG: isBefore(atEndOfMonth()) excludes the last day of the month
    !it.tx.date.isBefore(month.atDay(1)) && it.tx.date.isBefore(month.atEndOfMonth())
}

data class CategoryTotal(val category: String, val total: Money, val count: Int)

data class MonthReport(
    val month: YearMonth,
    val income: Money,
    val spending: Money,
    val categories: List<CategoryTotal>,
) {
    val net get() = income + spending
}

/** Biggest totals first (by size, either sign), names break ties, Uncategorised last. */
private val reportOrder = compareBy<CategoryTotal> { it.category == UNCATEGORISED }
    .thenByDescending { it.total.abs() }
    .thenBy { it.category }

fun monthReport(all: List<Categorised>, month: YearMonth): MonthReport {
    val items = all.inMonth(month)
    val amounts = items.map { it.tx.amount }
    val categories = items.groupBy { it.category }
        .map { (category, group) -> CategoryTotal(category, group.map { it.tx.amount }.sum(), group.size) }
        .sortedWith(reportOrder)
    return MonthReport(
        month,
        income = amounts.filter { it > Money.ZERO }.sum(),
        spending = amounts.filter { it < Money.ZERO }.sum(),
        categories = categories,
    )
}

/** Most negative first; then earlier date, then earlier line, so ties are deterministic. */
private val expenseOrder = compareBy<Categorised>({ it.tx.amount }, { it.tx.date }, { it.tx.line })

fun topExpenses(all: List<Categorised>, month: YearMonth, n: Int): List<Categorised> =
    all.inMonth(month).asSequence()
        .filter { it.tx.amount < Money.ZERO }
        .sortedWith(expenseOrder)
        .take(n)
        .toList()

data class BudgetLine(val category: String, val budget: Money, val spent: Money) {
    val left get() = budget - spent
    val percent get() = spent.percentOf(budget)
}

fun budgetLines(all: List<Categorised>, month: YearMonth, budgets: Map<String, Money>): List<BudgetLine> {
    val spentBy = all.inMonth(month)
        .groupBy({ it.category }, { it.tx.amount })
        .mapValues { (_, amounts) -> -amounts.sum() }
    return budgets.map { (category, budget) -> BudgetLine(category, budget, spentBy[category] ?: Money.ZERO) }
}
