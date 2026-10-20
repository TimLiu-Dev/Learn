fun percentOrThrow(text: String): Int {
    val n = text.toIntOrNull() ?: throw IllegalArgumentException("not a number: '$text'")
    require(n in 0..100) { "not a percentage: $n" }
    return n
}

fun percentOrNull(text: String): Int? {
    val n = text.toIntOrNull() ?: return null
    return if (n in 0..100) n else null
}

sealed interface PercentResult {
    data class Ok(val value: Int) : PercentResult
    data class NotANumber(val input: String) : PercentResult
    data class OutOfRange(val value: Int) : PercentResult
}

fun parsePercent(text: String): PercentResult {
    val n = text.toIntOrNull() ?: return PercentResult.NotANumber(text)
    return if (n in 0..100) PercentResult.Ok(n) else PercentResult.OutOfRange(n)
}

fun main() {
    for (input in listOf("42", "abc", "150")) {
        val a = try { percentOrThrow(input).toString() } catch (e: IllegalArgumentException) { "threw (${e.message})" }
        val b = percentOrNull(input)?.toString() ?: "null"
        val c = when (val r = parsePercent(input)) {
            is PercentResult.Ok -> "${r.value}%"
            is PercentResult.NotANumber -> "not a number"
            is PercentResult.OutOfRange -> "${r.value} is out of range"
        }
        println("$input: $a | $b | $c")
    }
}
