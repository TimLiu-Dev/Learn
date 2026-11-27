package budget

// Starter for Mini project M1 · Budget CLI. The spec is on the project page.
// Everything marked TODO is yours. Rename, split and restructure freely:
// the checker only looks at what the program prints and its exit code.

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

/**
 * The command line split into pieces, with nothing checked yet.
 * `report s.csv --month 2026-03` gives command "report", positionals [s.csv]
 * and options {--month=2026-03}. An option with no value maps to null.
 */
data class RawArgs(
    val command: String?,
    val positionals: List<String>,
    val options: Map<String, String?>,
    val help: Boolean,
)

fun splitArgs(args: List<String>): RawArgs {
    val positionals = mutableListOf<String>()
    val options = mutableMapOf<String, String?>()
    var i = 1
    while (i < args.size) {
        if (args[i].startsWith("--")) {
            options[args[i]] = args.getOrNull(i + 1)
            i += 2
        } else {
            positionals += args[i++]
        }
    }
    return RawArgs(args.firstOrNull(), positionals, options, help = "--help" in args || "-h" in args)
}

fun main(args: Array<String>) {
    val raw = splitArgs(args.toList())
    if (raw.help) {
        println(USAGE)
        return
    }
    // Milestone 1: replace this `when` with a sealed Command type built from RawArgs
    // (or a usage error), then run it. See the page for the design questions.
    when (raw.command) {
        "validate" -> TODO("Milestone 1: validate <statement.csv>")
        "categorise" -> TODO("Milestone 2: categorise")
        "report" -> TODO("Milestone 3: report")
        "top" -> TODO("Milestone 4: top")
        "budget" -> TODO("Milestone 4: budget")
        else -> TODO("Milestone 1: usage errors. Print 'error: ...' to stderr and exit 1")
    }
}
