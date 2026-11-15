fun interface Rule {
    fun check(password: String): Boolean
}

// A rule that needs at least n characters
fun minLength(n: Int): Rule = TODO("return a lambda: it becomes a Rule")

// At least one digit, anywhere
val hasDigit: Rule = Rule { pw -> TODO("pass a function reference to any") }

// The names of the rules this password breaks, in the map's order
fun failures(password: String, rules: Map<String, Rule>): List<String> =
    TODO("keep the entries whose rule fails, then take their keys")
