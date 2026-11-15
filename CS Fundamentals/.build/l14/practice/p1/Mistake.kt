fun interface Rule {
    fun check(password: String): Boolean
}

var limit = 0

fun minLength(n: Int): Rule {
    limit = n
    return Rule { it.length >= limit }   // every rule reads the same shared var
}

val hasDigit: Rule = Rule { pw -> pw.any(Char::isDigit) }

fun failures(password: String, rules: Map<String, Rule>): List<String> =
    rules.filterValues { !it.check(password) }.keys.toList()
