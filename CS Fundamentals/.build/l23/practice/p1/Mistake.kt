// One instruction per string: "iconst 5", "iload 0", "istore 2", "iadd", "isub", "imul", "ireturn".
fun execute(code: List<String>, vararg args: Int): Int {
    val locals = IntArray(8)
    args.copyInto(locals)
    val stack = ArrayDeque<Int>()
    for (line in code) {
        val parts = line.split(" ")
        when (parts[0]) {
            "iconst" -> stack.addLast(parts[1].toInt())
            "iload" -> stack.addLast(locals[parts[1].toInt()])
            "istore" -> locals[parts[1].toInt()] = stack.removeLast()
            "iadd" -> stack.addLast(stack.removeLast() + stack.removeLast())
            "isub" -> stack.addLast(stack.removeLast() - stack.removeLast())   // pops b first: computes b - a
            "imul" -> stack.addLast(stack.removeLast() * stack.removeLast())
            "ireturn" -> return stack.removeLast()
            else -> error("unknown instruction: $line")
        }
    }
    error("program ended without ireturn")
}
