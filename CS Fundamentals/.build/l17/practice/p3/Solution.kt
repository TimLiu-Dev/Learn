fun firstError(text: String): Int {
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    val open = ArrayDeque<Int>()                    // INDEXES of unclosed openers; top at the end
    for (i in text.indices) {
        when (text[i]) {
            '(', '[', '{' -> open.addLast(i)
            ')', ']', '}' -> {
                val top = open.removeLastOrNull() ?: return i       // nothing open
                if (text[top] != pairs[text[i]]) return i           // wrong kind of opener
            }
        }
    }
    return open.firstOrNull() ?: -1                 // the bottom of the stack is the leftmost
}
