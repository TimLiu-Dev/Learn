sealed interface Expr
data class Num(val value: Int) : Expr
data class Neg(val operand: Expr) : Expr
data class Bin(val op: Char, val left: Expr, val right: Expr) : Expr   // op is one of + - * /

fun compile(e: Expr): List<String> = TODO("emit the children's code first, then the node's instruction")

fun execute(code: List<String>): Int {
    val stack = ArrayDeque<Int>()
    for (instr in code) {
        TODO("push a number, or pop operands, combine them and push the result")
    }
    return stack.removeLast()
}
