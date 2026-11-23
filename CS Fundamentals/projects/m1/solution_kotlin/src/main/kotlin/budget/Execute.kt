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
fun execute(command: Command, read: ReadLines): Outcome {
    if (command is Command.Help) return Outcome.Done(USAGE.lines())
    val statementPath = when (command) {
        is Command.Validate -> command.statement
        is Command.Categorise -> command.statement
        is Command.Report -> command.statement
        is Command.Top -> command.statement
        is Command.Budget -> command.statement
        Command.Help -> error("handled above")
    }
    val statement = parseStatement(read(statementPath) ?: return Outcome.Failed("cannot read '$statementPath'"))
    if (command is Command.Validate) {
        return Outcome.Done(renderValidate(statement), exitCode = if (statement.problems.isEmpty()) 0 else 2)
    }

    val rulesPath = when (command) {
        is Command.Categorise -> command.rules
        is Command.Report -> command.rules
        is Command.Top -> command.rules
        is Command.Budget -> command.rules
        else -> error("validate and help are handled above")
    }
    val rules = when (val parsed = parseRules(read(rulesPath) ?: return Outcome.Failed("cannot read '$rulesPath'"))) {
        is Parsed.Ok -> parsed.value
        is Parsed.Bad -> return Outcome.Failed("$rulesPath ${parsed.reason}")
    }
    val items = categoriseAll(statement.transactions, rules)
    val warnings = statement.problems.size.let { bad ->
        if (bad == 0) emptyList() else listOf("warning: skipped $bad malformed line(s); run 'budget validate' for details")
    }

    val lines = when (command) {
        is Command.Categorise -> renderCategorise(items)
        is Command.Report -> renderReport(monthReport(items, command.month))
        is Command.Top -> renderTop(topExpenses(items, command.month, command.n), command.month, command.n)
        is Command.Budget -> {
            val budgets = when (val parsed = parseBudgets(read(command.budgets) ?: return Outcome.Failed("cannot read '${command.budgets}'"))) {
                is Parsed.Ok -> parsed.value
                is Parsed.Bad -> return Outcome.Failed("${command.budgets} ${parsed.reason}")
            }
            renderBudget(budgetLines(items, command.month, budgets), command.month)
        }
        else -> error("validate and help are handled above")
    }
    return Outcome.Done(lines, warnings)
}
