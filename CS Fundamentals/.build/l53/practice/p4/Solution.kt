// The arguments a program receives after bash expands `line`.
// vars are the shell's variables; args are the script's arguments ($1, $2, ...).
fun expand(line: String, vars: Map<String, String>, args: List<String>): List<String> =
    line.split(" ").flatMap { token ->
        when {
            token == "\"\$@\"" -> args
            token == "\$@" || token == "\$*" -> splitWords(args.joinToString(" "))
            token.startsWith("\"\$") && token.endsWith("\"") -> listOf(valueOf(token.drop(2).dropLast(1), vars))
            token.startsWith("\$") -> splitWords(valueOf(token.drop(1), vars))
            else -> listOf(token)
        }
    }

// set -u: an unset variable is an error, not an empty string
fun valueOf(name: String, vars: Map<String, String>): String =
    vars[name] ?: throw IllegalStateException("$name: unbound variable")

// Word splitting: runs of whitespace separate words, and empty pieces vanish.
fun splitWords(s: String): List<String> = s.split(Regex("\\s+")).filter { it.isNotEmpty() }
