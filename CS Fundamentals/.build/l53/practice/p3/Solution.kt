// grep -c: how many lines matched, and the exit status
data class GrepResult(val count: Int, val status: Int)

// How a process ended
sealed interface Ended
data class Exited(val code: Int) : Ended
data class Signalled(val signal: String) : Ended

val SIGNALS = mapOf("HUP" to 1, "INT" to 2, "KILL" to 9, "PIPE" to 13, "TERM" to 15)

fun grepC(pattern: Regex, lines: List<String>): GrepResult {
    val count = lines.count { pattern.containsMatchIn(it) }   // grep matches anywhere in the line
    return GrepResult(count, if (count > 0) 0 else 1)
}

// What $? shows. The sealed interface lets `when` cover every case without `else`.
fun status(ended: Ended): Int = when (ended) {
    is Exited -> ended.code
    is Signalled -> 128 + SIGNALS.getValue(ended.signal)
}
