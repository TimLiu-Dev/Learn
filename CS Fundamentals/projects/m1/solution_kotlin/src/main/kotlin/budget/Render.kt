package budget

import java.time.YearMonth

// Turning results into the exact text of the spec. Kept apart from the
// calculations so each can be tested on its own.

fun renderValidate(statement: Statement): List<String> =
    statement.problems.sortedBy { it.line }.map { "line ${it.line}: ${it.reason}" } +
        "${statement.transactions.size} valid, ${statement.problems.size} malformed"

fun renderCategorise(items: List<Categorised>): List<String> =
    items.map { (tx, category) -> "${tx.date} | ${tx.amount} | $category | ${tx.description}" } +
        "${items.size} transactions, ${items.count { it.category == UNCATEGORISED }} uncategorised"

fun renderReport(report: MonthReport): List<String> = buildList {
    add("Report ${report.month}")
    add("Income ${report.income}")
    add("Spending ${report.spending}")
    add("Net ${report.net}")
    if (report.categories.isEmpty()) add("No transactions")
    report.categories.forEach { add("${it.category} ${it.total} (${it.count})") }
}

fun renderTop(top: List<Categorised>, month: YearMonth, n: Int): List<String> = buildList {
    add("Top $n expenses $month")
    if (top.isEmpty()) add("No expenses")
    top.forEachIndexed { i, (tx, category) ->
        add("${i + 1} | ${tx.date} | ${tx.amount} | $category | ${tx.description}")
    }
}

fun renderBudget(lines: List<BudgetLine>, month: YearMonth): List<String> {
    val total = BudgetLine("Total", lines.map { it.budget }.sum(), lines.map { it.spent }.sum())
    return listOf("Budget $month") + (lines + total).map {
        "${it.category} | ${it.budget} | ${it.spent} | ${status(it.left)} | ${it.percent.toPlainString()}%"
    }
}

private fun status(left: Money) = when {
    left > Money.ZERO -> "$left under"
    left < Money.ZERO -> "${-left} over"
    else -> "on budget"
}
