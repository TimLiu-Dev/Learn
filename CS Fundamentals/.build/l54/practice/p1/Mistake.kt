class CalcError(message: String) : RuntimeException(message)

fun tokenize(src: String): List<String> {
    val tokens = mutableListOf<String>()
    var i = 0
    while (i < src.length) {
        val c = src[i]
        when {
            c == ' ' -> i++
            c in '0'..'9' -> {
                tokens += c.toString()      // one token per digit
                i++
            }
            c == '*' && i + 1 < src.length && src[i + 1] == '*' -> {
                tokens += "**"
                i += 2
            }
            c in "+-*/()" -> {
                tokens += c.toString()
                i++
            }
            else -> throw CalcError("unexpected '$c' at column ${i + 1}")
        }
    }
    return tokens
}
