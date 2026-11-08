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

// No else: the next case someone adds becomes a compile error here, not a wrong message.
fun describe(input: AgeInput): String = when (input) {
    is Valid -> "age ${input.years}"
    is NotANumber -> "not a number: ${input.text}"
    is OutOfRange -> "out of range: ${input.years}"
    Blank -> "please enter your age"
}

fun validAges(texts: List<String>): List<Int> =
    texts.map(::parseAge).filterIsInstance<Valid>().map { it.years }
