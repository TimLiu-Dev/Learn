import kotlin.system.exitProcess

// A well-behaved Unix tool: data in on stdin, results on stdout, problems on stderr,
// and an exit code that says how it went (0 ok, 1 some bad lines, 2 no input at all).
fun grade(score: Int): String = when {
    score >= 90 -> "A"
    score >= 80 -> "B"
    score >= 70 -> "C"
    else -> "F"
}

fun main() {
    var lines = 0
    var bad = 0
    generateSequence(::readLine).forEach { line ->
        lines++
        val score = line.trim().toIntOrNull()
        if (score == null || score !in 0..100) {
            System.err.println("line $lines: not a score: '$line'")
            bad++
        } else {
            println("$score ${grade(score)}")
        }
    }
    if (lines == 0) {
        System.err.println("no input")
        exitProcess(2)               // returns Nothing: the compiler knows nothing runs after it
    }
    exitProcess(if (bad > 0) 1 else 0)
}
