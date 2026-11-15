fun interface Rule {
    fun check(password: String): Boolean
}

// The returned rule captures n, so each call makes a rule with its own limit
fun minLength(n: Int): Rule = Rule { it.length >= n }

// Char::isDigit is an unbound reference: (Char) -> Boolean
val hasDigit: Rule = Rule { pw -> pw.any(Char::isDigit) }

fun failures(password: String, rules: Map<String, Rule>): List<String> =
    rules.filterValues { !it.check(password) }.keys.toList()
