package budget

import java.util.regex.PatternSyntaxException

const val UNCATEGORISED = "Uncategorised"

/** How a rule recognises a description. */
sealed interface Matcher {
    fun matches(description: String): Boolean

    data class Contains(val text: String) : Matcher {
        override fun matches(description: String) = description.contains(text, ignoreCase = true)
    }

    // Kotlin adds UNICODE_CASE to IGNORE_CASE, so /café/ also matches "CAFÉ".
    class Pattern(val regex: Regex) : Matcher {
        override fun matches(description: String) = regex.containsMatchIn(description)
        override fun toString() = "Pattern(/${regex.pattern}/)"
    }
}

data class Rule(val matcher: Matcher, val category: String)

/** Ordered rules: the first match wins. */
class Rules(val rules: List<Rule>) {
    fun categorise(description: String): String =
        rules.firstOrNull { it.matcher.matches(description) }?.category ?: UNCATEGORISED
}

/** All-or-nothing: one bad line rejects the whole file, naming that line. */
fun parseRules(lines: List<String>): Parsed<Rules> {
    val rules = mutableListOf<Rule>()
    for ((index, raw) in lines.withIndex()) {
        val line = raw.trim()
        if (line.isEmpty() || line.startsWith("#")) continue
        when (val rule = parseRule(line)) {
            is Parsed.Ok -> rules += rule.value
            is Parsed.Bad -> return Parsed.Bad("line ${index + 1}: ${rule.reason}")
        }
    }
    return Parsed.Ok(Rules(rules))
}

private fun parseRule(line: String): Parsed<Rule> {
    val arrow = line.lastIndexOf("->")
    if (arrow < 0) return Parsed.Bad("missing '->'")
    val pattern = line.substring(0, arrow).trim()
    val category = line.substring(arrow + 2).trim()
    if (category.isEmpty()) return Parsed.Bad("empty category")
    val isRegex = pattern.length >= 2 && pattern.startsWith("/") && pattern.endsWith("/")
    val body = if (isRegex) pattern.substring(1, pattern.length - 1) else pattern
    if (body.isEmpty()) return Parsed.Bad("empty pattern")
    if (!isRegex) return Parsed.Ok(Rule(Matcher.Contains(body), category))
    return try {
        Parsed.Ok(Rule(Matcher.Pattern(Regex(body, RegexOption.IGNORE_CASE)), category))
    } catch (e: PatternSyntaxException) {
        Parsed.Bad("invalid regex '$pattern'")
    }
}
