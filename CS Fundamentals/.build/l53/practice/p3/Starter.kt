// grep -c: how many lines matched, and the exit status
data class GrepResult(val count: Int, val status: Int)

// How a process ended
sealed interface Ended
data class Exited(val code: Int) : Ended
data class Signalled(val signal: String) : Ended

val SIGNALS = mapOf("HUP" to 1, "INT" to 2, "KILL" to 9, "PIPE" to 13, "TERM" to 15)

fun grepC(pattern: Regex, lines: List<String>): GrepResult =
    TODO("count lines where the pattern is found; status 0 if any, else 1")

// What $? shows
fun status(ended: Ended): Int =
    TODO("an exit code passes through; a signal gives 128 + its number")
