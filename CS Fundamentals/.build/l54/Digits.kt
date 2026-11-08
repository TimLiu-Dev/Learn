// Exercise 1: scan a number that may use '_' between digits, like Kotlin and Java literals.
// Returns the value and the index just past the literal. src[start] must be a digit.
fun scanNumber(src: String, start: Int): Pair<Int, Int> {
    var i = start
    val digits = StringBuilder()
    while (i < src.length && (src[i] in '0'..'9' || src[i] == '_')) {
        if (src[i] != '_') digits.append(src[i])
        i++
    }
    if (src[i - 1] == '_') throw CalcError("'_' must be followed by a digit at col ${i}")
    val value = digits.toString().toIntOrNull() ?: throw CalcError("number too large at col ${start + 1}")
    return value to i
}

fun main() {
    for (s in listOf("1_000_000", "1__0", "7", "1_", "2_147_483_648", "12_3+4")) {
        try {
            val (value, end) = scanNumber(s, 0)
            println("%-14s -> %d, rest \"%s\"".format(s, value, s.substring(end)))
        } catch (e: CalcError) {
            println("%-14s -> CalcError: %s".format(s, e.message))
        }
    }
}
