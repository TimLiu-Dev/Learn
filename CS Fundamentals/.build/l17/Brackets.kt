fun isBalanced(text: String): Boolean {
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    val open = ArrayDeque<Char>()                   // kotlin.collections.ArrayDeque
    for (c in text) {
        when (c) {
            '(', '[', '{' -> open.addLast(c)        // push every opener
            ')', ']', '}' -> {
                val top = open.removeLastOrNull() ?: return false   // closer with nothing open
                if (top != pairs[c]) return false   // closer doesn't match the latest opener
            }
        }
    }
    return open.isEmpty()                           // anything left open is unbalanced
}

fun main() {
    for (s in listOf("{[()]}", "f(a[0], {b})", "([)]", "((", "())", ""))
        println("%-14s %s".format("\"$s\"", isBalanced(s)))
}
