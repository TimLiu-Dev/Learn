package search

/** Runs one command line against an index and returns its output, without the trailing blank line. */
class Commands(private val index: Index) {

    fun run(line: String): String {
        val parts = line.trim().split(Regex("\\s+"), limit = 2)
        val rest = parts.getOrElse(1) { "" }
        return when (val word = parts[0]) {
            "stats", "doc" -> TODO("the '$word' command (milestone 1)")
            "tokens" -> TODO("the '$word' command (milestone 2)")
            "q", "or" -> TODO("the '$word' command (milestone 3)")
            "prefix", "top" -> TODO("the '$word' command (milestone 4)")
            "bench" -> TODO("the '$word' command (milestone 5)")
            else -> "error: unknown command '$word'"
        }
    }
}
