package budget

/** What running a command produced. The shell in Main.kt turns it into streams and an exit code. */
sealed interface Outcome {
    data class Done(val lines: List<String>, val warnings: List<String> = emptyList(), val exitCode: Int = 0) : Outcome
    data class Failed(val message: String) : Outcome
}

/** Reads a file as lines, or null if it can't. Injected so tests never touch the disk. */
typealias ReadLines = (path: String) -> List<String>?

/**
 * The pure core: given a checked command and a way to read files, compute the
 * outcome. No printing, no exiting, no real files.
 */
fun execute(command: Command, read: ReadLines): Outcome = when (command) {
    Command.Help -> Outcome.Done(USAGE.lines())
    is Command.Validate -> {
        val statement = read(command.statement)?.let(::parseStatement)
        if (statement == null) Outcome.Failed("cannot read '${command.statement}'")
        else Outcome.Done(renderValidate(statement), exitCode = if (statement.problems.isEmpty()) 0 else 2)
    }
    is Command.WithRules -> executeWithRules(command, read)
}

private fun executeWithRules(command: Command.WithRules, read: ReadLines): Outcome {
    val statement = parseStatement(read(command.statement) ?: return cannotRead(command.statement))
    val rules = when (val parsed = parseRules(read(command.rules) ?: return cannotRead(command.rules))) {
        is Parsed.Ok -> parsed.value
        is Parsed.Bad -> return Outcome.Failed("${command.rules} ${parsed.reason}")
    }
    val items = categoriseAll(statement.transactions, rules)
    val lines = when (command) {
        is Command.Categorise -> renderCategorise(items)
        is Command.Report -> renderReport(monthReport(items, command.month))
        is Command.Top -> renderTop(topExpenses(items, command.month, command.n), command.month, command.n)
        is Command.Budget -> {
            val budgets = when (val parsed = parseBudgets(read(command.budgets) ?: return cannotRead(command.budgets))) {
                is Parsed.Ok -> parsed.value
                is Parsed.Bad -> return Outcome.Failed("${command.budgets} ${parsed.reason}")
            }
            renderBudget(budgetLines(items, command.month, budgets), command.month)
        }
    }
    val bad = statement.problems.size
    val warnings = if (bad == 0) emptyList() else listOf("warning: skipped $bad malformed line(s); run 'budget validate' for details")
    return Outcome.Done(lines, warnings)
}

private fun cannotRead(path: String) = Outcome.Failed("cannot read '$path'")
