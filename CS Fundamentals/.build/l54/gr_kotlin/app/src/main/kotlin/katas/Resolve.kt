package katas

// A static check, before anything runs: every variable is defined before it's used.
// The compiler does this anyway when it gives names their slots; the interpreter needs it as a separate pass.
fun resolve(p: Program) {
    val defined = HashSet<String>()
    fun check(e: Expr) {
        when (e) {
            is Num -> {}
            is Var -> if (e.name !in defined) throw CalcError("undefined variable '${e.name}' at ${e.at.at}")
            is Neg -> check(e.operand)
            is Bin -> { check(e.left); check(e.right) }
        }
    }
    for (let in p.lets) {
        check(let.value)
        defined += let.name
    }
    check(p.result)
}
