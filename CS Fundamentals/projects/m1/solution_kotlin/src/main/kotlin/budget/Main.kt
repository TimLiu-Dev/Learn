package budget

import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

// The thin IO shell: the only file that reads files, prints or exits.

fun main(args: Array<String>) {
    val outcome = when (val parsed = parseCommand(args.toList())) {
        is Parsed.Ok -> execute(parsed.value, ::readLinesOrNull)
        is Parsed.Bad -> Outcome.Failed(parsed.reason)
    }
    when (outcome) {
        is Outcome.Done -> {
            outcome.lines.forEach(::println)
            outcome.warnings.forEach(System.err::println)
            exitProcess(outcome.exitCode)
        }
        is Outcome.Failed -> {
            System.err.println("error: ${outcome.message}")
            exitProcess(1)
        }
    }
}

/** File.readLines reads UTF-8 and splits on \n, \r\n and \r. L22 explains files properly. */
private fun readLinesOrNull(path: String): List<String>? =
    try {
        File(path).readLines()
    } catch (e: IOException) {
        null
    }
