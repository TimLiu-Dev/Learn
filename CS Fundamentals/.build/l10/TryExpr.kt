fun parseOr(text: String, fallback: Int): Int {
    val n = try {
        text.toInt()                       // value of the try block...
    } catch (e: NumberFormatException) {
        fallback                           // ...or of the catch block
    }
    return n
}

fun main() {
    println(parseOr("42", -1))
    println(parseOr("4 2", -1))

    val settings = mapOf("host" to "localhost")
    val host = settings["host"] ?: throw IllegalArgumentException("host is required")
    println(host)
    val port = settings["port"] ?: throw IllegalArgumentException("port is required")
    println(port)                          // never reached
}
