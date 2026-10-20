fun parsePort(text: String): Int = text.toInt()

fun loadConfig(name: String): Int {
    try {
        return parsePort("80x")
    } catch (e: NumberFormatException) {
        // translate to an exception that makes sense at THIS level, keeping the cause
        throw IllegalStateException("could not load config '$name'", e)
    }
}

fun main() {
    loadConfig("app.conf")
}
