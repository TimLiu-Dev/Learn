// Strips the quotes, then splits every value: quoting changes nothing.
fun expand(line: String, vars: Map<String, String>, args: List<String>): List<String> =
    line.split(" ").flatMap { token ->
        val bare = token.removeSurrounding("\"")
        when {
            bare == "\$@" || bare == "\$*" -> splitWords(args.joinToString(" "))
            bare.startsWith("\$") -> splitWords(valueOf(bare.drop(1), vars))
            else -> listOf(token)
        }
    }

fun valueOf(name: String, vars: Map<String, String>): String =
    vars[name] ?: throw IllegalStateException("$name: unbound variable")

fun splitWords(s: String): List<String> = s.split(Regex("\\s+")).filter { it.isNotEmpty() }
