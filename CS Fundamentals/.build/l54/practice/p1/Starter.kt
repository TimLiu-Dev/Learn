class CalcError(message: String) : RuntimeException(message)

fun tokenize(src: String): List<String> {
    val tokens = mutableListOf<String>()
    var i = 0
    while (i < src.length) {
        val c = src[i]
        TODO("skip a space, scan a number, scan ** or a one-character operator, or throw CalcError")
    }
    return tokens
}
