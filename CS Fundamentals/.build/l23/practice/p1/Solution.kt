// One instruction per string: "iconst 5", "iload 0", "istore 2", "iadd", "isub", "imul", "ireturn".
fun execute(code: List<String>, vararg args: Int): Int {
    val locals = IntArray(8)
    args.copyInto(locals)                  // parameters live in slots 0, 1, 2, …
    val stack = ArrayDeque<Int>()          // the operand stack: top is the last element
    for (line in code) {
        val parts = line.split(" ")
        when (parts[0]) {
            "iconst" -> stack.addLast(parts[1].toInt())
            "iload" -> stack.addLast(locals[parts[1].toInt()])
            "istore" -> locals[parts[1].toInt()] = stack.removeLast()
            "iadd", "isub", "imul" -> {
                val right = stack.removeLast()   // pushed last, so popped first
                val left = stack.removeLast()
                stack.addLast(
                    when (parts[0]) {
                        "iadd" -> left + right
                        "isub" -> left - right
                        else -> left * right
                    }
                )
            }
            "ireturn" -> return stack.removeLast()
            else -> error("unknown instruction: $line")
        }
    }
    error("program ended without ireturn")
}
