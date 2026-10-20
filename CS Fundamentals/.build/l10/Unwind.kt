fun parsePort(text: String): Int {
    println("parsePort: parsing \"$text\"")
    val port = text.toInt()            // throws NumberFormatException
    println("parsePort: done")         // never runs
    return port
}

fun startServer(config: String): String {
    try {
        val port = parsePort(config)
        return "listening on $port"
    } finally {
        println("startServer: finally runs while unwinding")
    }
}

fun main() {
    try {
        println(startServer("80x"))
    } catch (e: NumberFormatException) {
        println("main caught: ${e.message}")
    }
    println("main continues")
}
