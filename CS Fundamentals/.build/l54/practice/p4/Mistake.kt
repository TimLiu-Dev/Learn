sealed interface Expr
data class Num(val value: Int) : Expr
data class Neg(val operand: Expr) : Expr
data class Bin(val op: Char, val left: Expr, val right: Expr) : Expr   // op is one of + - * /
fun compile(e: Expr): List<String> = when (e) {
    is Num -> listOf("push ${e.value}")
    is Neg -> compile(e.operand) + "neg"
    is Bin -> compile(e.left) + compile(e.right) + when (e.op) {   // post-order, just like eval
        '+' -> "add"
        '-' -> "sub"
        '*' -> "mul"
        else -> "div"
    }
}

fun execute(code: List<String>): Int {
    val stack = ArrayDeque<Int>()
    for (instr in code) {
        if (instr.startsWith("push ")) {
            stack.addLast(instr.removePrefix("push ").toInt())
            continue
        }
        if (instr == "neg") {
            stack.addLast(-stack.removeLast())
            continue
        }
        val a = stack.removeLast()
        val b = stack.removeLast()
        stack.addLast(when (instr) {
            "add" -> a + b
            "sub" -> a - b
            "mul" -> a * b
            "div" -> a / b
            else -> error("unknown instruction '$instr'")
        })
    }
    return stack.removeLast()
}
