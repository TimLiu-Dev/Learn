// Design 1: throw. The caller must not pass garbage.
fun parsePortOrThrow(text: String): Int {
    val n = text.toIntOrNull() ?: throw IllegalArgumentException("not a number: '$text'")
    require(n in 1..65535) { "out of range: $n" }
    return n
}

// Design 2: return null. "No port" is a normal outcome.
fun parsePortOrNull(text: String): Int? {
    val n = text.toIntOrNull() ?: return null
    return if (n in 1..65535) n else null
}

// Design 3: a sealed result. Failure is normal AND the caller needs the reason.
sealed interface PortResult {
    data class Ok(val port: Int) : PortResult
    data class NotANumber(val input: String) : PortResult
    data class OutOfRange(val value: Int) : PortResult
}

fun parsePort(text: String): PortResult {
    val n = text.toIntOrNull() ?: return PortResult.NotANumber(text)
    return if (n in 1..65535) PortResult.Ok(n) else PortResult.OutOfRange(n)
}

fun main() {
    for (input in listOf("8080", "http", "99999")) {
        val thrown = try {
            parsePortOrThrow(input).toString()
        } catch (e: IllegalArgumentException) {
            "threw: ${e.message}"
        }
        val nullable = parsePortOrNull(input) ?: "null"
        val message = when (val r = parsePort(input)) {
            is PortResult.Ok -> "port ${r.port}"
            is PortResult.NotANumber -> "'${r.input}' is not a number"
            is PortResult.OutOfRange -> "${r.value} is outside 1..65535"
        }
        println("$input -> $thrown | $nullable | $message")
    }

    // Kotlin's built-in Result wraps "value or exception"
    val r: Result<Int> = runCatching { parsePortOrThrow("99999") }
    println("isSuccess=${r.isSuccess}, error=${r.exceptionOrNull()?.message}, or default=${r.getOrDefault(80)}")
}
