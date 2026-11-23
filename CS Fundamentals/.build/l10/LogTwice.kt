// A stand-in for a real logger: in production each call would also print the whole stack trace.
fun log(msg: String) = println("ERROR $msg")

fun readPort(text: String): Int =
    text.toIntOrNull() ?: throw IllegalArgumentException("not a port: '$text'")

fun loadNoisy(text: String): Int =
    try {
        readPort(text)
    } catch (e: IllegalArgumentException) {
        log("loading config failed: ${e.message}")    // logs...
        throw e                                         // ...AND rethrows: the caller will log it again
    }

fun loadQuiet(text: String): Int =
    try {
        readPort(text)
    } catch (e: IllegalArgumentException) {
        throw IllegalStateException("config 'port' is invalid", e)   // add context, keep the cause, don't log
    }

fun handleRequest(load: (String) -> Int) {
    try {
        load("80a")
    } catch (e: Exception) {                            // the top level: the ONE place that logs
        log("request failed: ${e.message}" + (e.cause?.let { " (cause: ${it.message})" } ?: ""))
    }
}

fun main() {
    handleRequest(::loadNoisy)
    println("---")
    handleRequest(::loadQuiet)
}
