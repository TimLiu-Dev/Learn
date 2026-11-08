// Exercise 4: constant folding, a compile-time optimisation on the tree.
fun fold(e: Expr): Expr = when (e) {
    is Num, is Var -> e
    is Neg -> when (val inner = fold(e.operand)) {
        is Num -> Num(-inner.value)
        else -> Neg(inner, e.at)
    }
    is Bin -> {
        val l = fold(e.left)
        val r = fold(e.right)
        if (l is Num && r is Num) {
            try { Num(arith(e.op, l.value, r.value, e.at.at)) }
            catch (err: CalcError) { Bin(e.op, l, r, e.at) }   // like javac: 1 / 0 stays, and fails at run time
        } else Bin(e.op, l, r, e.at)
    }
}

fun fold(p: Program) = Program(p.lets.map { Let(it.name, fold(it.value)) }, fold(p.result))

fun outcome(run: () -> Int): String = try { run().toString() } catch (e: CalcError) { "CalcError: ${e.message}" }

fun main() {
    val src = "let x = 2 * 3 + 4; x * (10 - 8)"
    print(disassemble(Compiler().compile(parse(src))))
    println("-- folded: ${show(fold(parse(src))).replace("\n", "; ")}")
    print(disassemble(Compiler().compile(fold(parse(src)))))
    for (s in listOf(src, "1 / 0 + 2", "let y = -(2 ^ 3); y - -1", "2147483647 + 1 + x")) {
        val plain = outcome { execute(Compiler().compile(parse(s))) }
        val folded = outcome { execute(Compiler().compile(fold(parse(s)))) }
        println("%-32s %-40s same=%s".format(s, folded, plain == folded))
    }
}
