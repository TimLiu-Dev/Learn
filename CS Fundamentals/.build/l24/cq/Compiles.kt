var compiled = 0

fun compile(pattern: String): Regex {
    compiled++
    return Regex(pattern)
}

class Validator {
    val word = compile("[a-z]+")
    val digits get() = compile("[0-9]+")
    val upper by lazy { compile("[A-Z]+") }
}

fun main() {
    val v = Validator()
    repeat(3) {
        v.word.matches("abc")
        v.digits.matches("123")
        v.upper.matches("ABC")
    }
    println(compiled)
}
