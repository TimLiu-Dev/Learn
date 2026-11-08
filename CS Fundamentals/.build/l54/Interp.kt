// Calc's tree-walking interpreter: evaluate the children first, then the node (post-order).
fun eval(e: Expr, env: Map<String, Int>): Int = when (e) {
    is Num -> e.value
    is Var -> env[e.name] ?: throw CalcError("undefined variable '${e.name}' at ${e.at.at}")
    is Neg -> -eval(e.operand, env)
    is Bin -> arith(e.op, eval(e.left, env), eval(e.right, env), e.at.at)
}

fun interpret(p: Program): Int {
    val env = HashMap<String, Int>()                   // the environment: name -> value
    for (let in p.lets) env[let.name] = eval(let.value, env)
    return eval(p.result, env)
}

// The arithmetic both back ends share. Int wraps around on overflow, exactly like the JVM's iadd (L2).
fun arith(op: T, a: Int, b: Int, at: String): Int = when (op) {
    T.PLUS -> a + b
    T.MINUS -> a - b
    T.STAR -> a * b
    T.SLASH -> if (b == 0) throw CalcError("division by zero at $at") else a / b
    T.PERCENT -> if (b == 0) throw CalcError("division by zero at $at") else a % b
    T.CARET -> if (b < 0) throw CalcError("negative exponent at $at") else pow(a, b)
    else -> error("not an operator: $op")
}

private fun pow(base: Int, exp: Int): Int {            // square-and-multiply, as in L48's powMod
    var result = 1; var b = base; var e = exp
    while (e > 0) {
        if (e and 1 == 1) result *= b
        b *= b; e = e shr 1
    }
    return result
}
