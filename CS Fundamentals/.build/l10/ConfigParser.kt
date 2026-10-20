import java.io.BufferedReader
import java.io.StringReader

class ConfigFormatException(val lineNo: Int, line: String) :
    Exception("line $lineNo: missing '=' in \"$line\"")

sealed interface LineResult {
    data class Entry(val key: String, val value: String) : LineResult
    data class BadValue(val key: String, val reason: String) : LineResult
    data class Malformed(val message: String) : LineResult
    data object Skipped : LineResult
}

val INT_KEYS = setOf("port", "timeout", "retries")

// Low level: splitting a line either works or the line is structurally broken.
fun splitEntry(lineNo: Int, line: String): Pair<String, String> {
    val eq = line.indexOf('=')
    if (eq < 0) throw ConfigFormatException(lineNo, line)
    return line.substring(0, eq).trim() to line.substring(eq + 1).trim()
}

// Boundary: turn every outcome, including the exception, into a value.
fun parseLine(lineNo: Int, raw: String): LineResult {
    val line = raw.trim()
    if (line.isEmpty() || line.startsWith("#")) return LineResult.Skipped
    val (key, value) = try {
        splitEntry(lineNo, line)
    } catch (e: ConfigFormatException) {
        return LineResult.Malformed(e.message ?: "malformed")
    }
    if (value.isEmpty()) return LineResult.BadValue(key, "value is empty")
    if (key in INT_KEYS && value.toIntOrNull() == null) {
        return LineResult.BadValue(key, "'$value' is not a whole number")
    }
    return LineResult.Entry(key, value)
}

fun main() {
    val text = """
        # server config
        port=8080
        timeout=abc
        host=
        garbage
        retries = 3
    """.trimIndent()

    var ok = 0
    var bad = 0
    var malformed = 0
    var skipped = 0
    BufferedReader(StringReader(text)).use { reader ->
        var lineNo = 0
        while (true) {
            val raw = reader.readLine() ?: break
            lineNo++
            when (val r = parseLine(lineNo, raw)) {
                is LineResult.Entry -> { ok++; println("line $lineNo: ${r.key} = ${r.value}") }
                is LineResult.BadValue -> { bad++; println("line $lineNo: ${r.key}: ${r.reason}") }
                is LineResult.Malformed -> { malformed++; println(r.message) }
                LineResult.Skipped -> { skipped++; println("line $lineNo: skipped") }
            }
        }
    }
    println("summary: $ok ok, $bad bad values, $malformed malformed, $skipped skipped")
}
