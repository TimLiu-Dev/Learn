sealed interface Expr
data class Num(val value: Int) : Expr
data class Var(val name: String) : Expr
data class Neg(val operand: Expr) : Expr
data class Bin(val op: Char, val left: Expr, val right: Expr) : Expr   // op is one of + - * / %

data class Let(val name: String, val value: Expr)
data class Program(val lets: List<Let>, val result: Expr)

class CalcError(message: String) : RuntimeException(message)

fun eval(e: Expr, env: Map<String, Int>): Int = TODO("one when branch per node type")

fun run(p: Program): Int = TODO("fill a HashMap from the lets, then evaluate the result")
