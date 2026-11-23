package budget

/** Monthly budget per category, in file order. */
fun parseBudgets(lines: List<String>): Parsed<Map<String, Money>> {
    val budgets = LinkedHashMap<String, Money>() // keeps the file's order for the output
    for ((index, raw) in lines.withIndex()) {
        val line = raw.trim()
        if (line.isEmpty() || line.startsWith("#")) continue
        val reason = parseBudgetLine(line, budgets)
        if (reason != null) return Parsed.Bad("line ${index + 1}: $reason")
    }
    return Parsed.Ok(budgets)
}

/** Adds one line to [budgets]; returns the reason it's bad, or null. */
private fun parseBudgetLine(line: String, budgets: MutableMap<String, Money>): String? {
    val equals = line.lastIndexOf('=')
    if (equals < 0) return "missing '='"
    val category = line.substring(0, equals).trim()
    val amountText = line.substring(equals + 1).trim()
    if (category.isEmpty()) return "empty category"
    val amount = Money.parse(amountText) ?: return "invalid amount '$amountText'"
    if (amount <= Money.ZERO) return "budget must be positive"
    if (category in budgets) return "duplicate category '$category'"
    budgets[category] = amount
    return null
}
