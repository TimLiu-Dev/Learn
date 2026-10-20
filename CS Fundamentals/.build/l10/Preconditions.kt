class Turnstile {
    var open = false
    var passes = 0
        private set

    fun pass(ticket: Int) {
        require(ticket > 0) { "ticket must be positive, was $ticket" }   // bad ARGUMENT
        check(open) { "turnstile is closed" }                            // bad STATE
        passes++
    }
}

fun describe(code: Int): String = when (code) {
    1 -> "ok"
    2 -> "retry"
    else -> error("unknown status code $code")                          // "can't happen"
}

fun attempt(label: String, action: () -> Unit) {
    try {
        action()
        println("$label: ok")
    } catch (e: Exception) {
        println("$label: ${e::class.simpleName}: ${e.message}")
    }
}

fun main() {
    val t = Turnstile()
    attempt("pass(-5)") { t.pass(-5) }
    attempt("pass(7) while closed") { t.pass(7) }
    t.open = true
    attempt("pass(7) while open") { t.pass(7) }
    attempt("describe(9)") { describe(9) }
}
