// One instruction per string: "iconst 5", "iload 0", "istore 2", "iadd", "isub", "imul", "ireturn".
fun execute(code: List<String>, vararg args: Int): Int {
    val locals = IntArray(8)
    args.copyInto(locals)                  // parameters live in slots 0, 1, 2, …
    val stack = ArrayDeque<Int>()          // the operand stack: top is the last element
    for (line in code) {
        val parts = line.split(" ")
        when (parts[0]) {
            "iconst" -> stack.addLast(parts[1].toInt())
            "iload" -> TODO("push the int in local slot parts[1]")
            "istore" -> TODO("pop into local slot parts[1]")
            "iadd" -> TODO("pop two ints, push their sum")
            "isub" -> TODO("pop two ints, push first-pushed minus last-pushed")
            "imul" -> TODO("pop two ints, push their product")
            "ireturn" -> TODO("pop the result and return it")
            else -> error("unknown instruction: $line")
        }
    }
    error("program ended without ireturn")
}
