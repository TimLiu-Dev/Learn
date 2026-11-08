sealed interface AgeInput
data class Valid(val years: Int) : AgeInput
data class NotANumber(val text: String) : AgeInput
data class OutOfRange(val years: Int) : AgeInput
data object Blank : AgeInput

fun parseAge(text: String): AgeInput {
    val t = text.trim()
    if (t.isEmpty()) return Blank
    val n = t.toIntOrNull() ?: return NotANumber(t)
    return if (n in 0..150) Valid(n) else OutOfRange(n)
}

fun describe(input: AgeInput): String = when (input) {
    is Valid -> "age ${input.years}"
    is NotANumber -> "not a number: ${input.text}"
    else -> "please enter your age"
}

fun validAges(texts: List<String>): List<Int> =
    texts.map(::parseAge).filterIsInstance<Valid>().map { it.years }
