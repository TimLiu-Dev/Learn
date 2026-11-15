sealed interface Expr
data class Num(val value: Int) : Expr
data class Var(val name: String) : Expr
data class Neg(val operand: Expr) : Expr
data class Bin(val op: Char, val left: Expr, val right: Expr) : Expr   // op is one of + - * / %

data class Let(val name: String, val value: Expr)
data class Program(val lets: List<Let>, val result: Expr)

class CalcError(message: String) : RuntimeException(message)

fun eval(e: Expr, env: Map<String, Int>): Int = when (e) {
    is Num -> e.value
    is Var -> env[e.name] ?: throw CalcError("undefined variable '${e.name}'")
    is Neg -> -eval(e.operand, env)
    is Bin -> {
        val a = eval(e.left, env)
        val b = eval(e.right, env)
        when (e.op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '%' -> a % b
            else -> throw CalcError("unknown operator '${e.op}'")
        }
    }
}

fun run(p: Program): Int {
    val env = HashMap<String, Int>()
    for (let in p.lets) env[let.name] = eval(let.value, env)
    return eval(p.result, env)
}
