package budget

import java.time.YearMonth
import java.time.format.DateTimeParseException

/** Everything the user can ask for, already checked. */
sealed interface Command {
    data object Help : Command
    data class Validate(val statement: String) : Command

    /** The commands that categorise transactions, so they all need a statement and rules. */
    sealed interface WithRules : Command {
        val statement: String
        val rules: String
    }

    data class Categorise(override val statement: String, override val rules: String) : WithRules
    data class Report(override val statement: String, override val rules: String, val month: YearMonth) : WithRules
    data class Top(override val statement: String, override val rules: String, val month: YearMonth, val n: Int) : WithRules
    data class Budget(
        override val statement: String,
        override val rules: String,
        val budgets: String,
        val month: YearMonth,
    ) : WithRules
}

val USAGE = """
    Usage: budget <command> <statement.csv> [options]

    Commands:
      validate   <statement.csv>                          report malformed lines (exit 2 if any)
      categorise <statement.csv> --rules FILE             label every transaction
      report     <statement.csv> --rules FILE --month YYYY-MM
                                                          income, spending and totals per category
      top        <statement.csv> --rules FILE --month YYYY-MM [--n N]
                                                          the N biggest expenses (default 5)
      budget     <statement.csv> --rules FILE --budgets FILE --month YYYY-MM
                                                          spending against each budget

    Options:
      -h, --help   show this help
""".trimIndent()

/** Which options each command accepts; all but --n are required. */
private val OPTIONS = mapOf(
    "validate" to setOf(),
    "categorise" to setOf("--rules"),
    "report" to setOf("--rules", "--month"),
    "top" to setOf("--rules", "--month", "--n"),
    "budget" to setOf("--rules", "--budgets", "--month"),
)

private val MONTH_SHAPE = Regex("""\d{4}-\d{2}""")

fun parseCommand(args: List<String>): Parsed<Command> {
    if ("--help" in args || "-h" in args) return Parsed.Ok(Command.Help)
    val name = args.firstOrNull() ?: return Parsed.Bad("missing command (try 'budget --help')")
    val allowed = OPTIONS[name] ?: return Parsed.Bad("unknown command '$name'")
    var statement: String? = null
    val options = mutableMapOf<String, String>()
    var i = 1
    while (i < args.size) {
        val arg = args[i]
        when {
            arg.startsWith("--") -> {
                if (arg !in allowed) return Parsed.Bad("unknown option '$arg' for $name")
                val value = args.getOrNull(i + 1) ?: return Parsed.Bad("missing value for $arg")
                options[arg] = value
                i += 2
                continue
            }
            statement == null -> statement = arg
            else -> return Parsed.Bad("unexpected argument '$arg'")
        }
        i++
    }
    if (statement == null) return Parsed.Bad("missing statement file")
    (allowed - "--n").firstOrNull { it !in options }?.let { return Parsed.Bad("missing $it") }

    val month = options["--month"]?.let { parseMonth(it) ?: return Parsed.Bad("invalid --month '$it' (expected YYYY-MM)") }
    val n = options["--n"]?.let { text ->
        text.toIntOrNull()?.takeIf { it > 0 } ?: return Parsed.Bad("invalid --n '$text' (expected a positive integer)")
    } ?: 5
    val rules = options["--rules"]
    return Parsed.Ok(
        when (name) {
            "validate" -> Command.Validate(statement)
            "categorise" -> Command.Categorise(statement, rules!!)
            "report" -> Command.Report(statement, rules!!, month!!)
            "top" -> Command.Top(statement, rules!!, month!!, n)
            else -> Command.Budget(statement, rules!!, options.getValue("--budgets"), month!!)
        }
    )
}

private fun parseMonth(text: String): YearMonth? =
    if (!MONTH_SHAPE.matches(text)) null
    else try {
        YearMonth.parse(text)
    } catch (e: DateTimeParseException) {
        null
    }
