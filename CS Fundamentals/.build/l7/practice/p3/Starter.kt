sealed interface AgeInput
data class Valid(val years: Int) : AgeInput
data class NotANumber(val text: String) : AgeInput
data class OutOfRange(val years: Int) : AgeInput    // added last week
data object Blank : AgeInput

fun parseAge(text: String): AgeInput = TODO("Blank, NotANumber, OutOfRange (outside 0..150) or Valid")

fun describe(input: AgeInput): String = when (input) {
    is Valid -> "age ${input.years}"
    is NotANumber -> "not a number: ${input.text}"
    else -> "please enter your age"                 // written before OutOfRange existed
}

fun validAges(texts: List<String>): List<Int> = TODO("parse each text, keep only the Valid ones")
