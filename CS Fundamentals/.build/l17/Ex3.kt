fun evalPostfix(expr: String): Int {
    val stack = ArrayDeque<Int>()
    for (token in expr.split(" ").filter { it.isNotEmpty() }) {
        val number = token.toIntOrNull()
        if (number != null) { stack.addLast(number); continue }
        require(stack.size >= 2) { "not enough operands for '$token'" }
        val right = stack.removeLast()              // popped first = the RIGHT operand
        val left = stack.removeLast()
        stack.addLast(when (token) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            else -> throw IllegalArgumentException("unknown token '$token'")
        })
    }
    require(stack.size == 1) { "expected one result, stack is $stack" }
    return stack.removeLast()
}

fun main() {
    for (e in listOf("3 4 +", "3 4 + 2 *", "10 2 8 * + 3 -", "7 2 -", "1 +", "1 2", "2 3 ^")) {
        try { println("$e = ${evalPostfix(e)}") }
        catch (ex: IllegalArgumentException) { println("$e -> error: ${ex.message}") }
    }
}
