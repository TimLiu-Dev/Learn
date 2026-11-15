fun firstError(text: String): Int {
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    val open = ArrayDeque<Int>()
    for (i in text.indices) {
        when (text[i]) {
            '(', '[', '{' -> open.addLast(i)
            ')', ']', '}' -> {
                val top = open.removeLastOrNull() ?: return i
                if (text[top] != pairs[text[i]]) return i
            }
        }
    }
    return open.lastOrNull() ?: -1                  // the top: the most recent opener
}
