fun parsePort(text: String): Int = text.toInt()

fun startServer(config: String): String = "listening on ${parsePort(config)}"

fun main() {
    println(startServer("80x"))        // nobody catches it
    println("never printed")
}
