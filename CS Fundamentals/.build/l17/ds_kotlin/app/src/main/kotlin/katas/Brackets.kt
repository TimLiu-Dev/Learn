package katas

private val PAIRS = mapOf(')' to '(', ']' to '[', '}' to '{')

fun isBalanced(text: String): Boolean {
    val open = MyStack<Char>()
    for (c in text) {
        if (c in PAIRS.values) open.push(c)
        else if (c in PAIRS.keys) {
            if (open.isEmpty() || open.pop() != PAIRS[c]) return false
        }
    }
    return open.isEmpty()
}
