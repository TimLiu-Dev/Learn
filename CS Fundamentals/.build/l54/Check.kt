// A checker for a tiny typed cousin of Calc: one more walk over the tree,
// before anything runs. It resolves every name and works out every type.
enum class Type { INT, BOOL }

sealed interface E
data class Num(val v: Int) : E
data class Bool(val v: Boolean) : E
data class Var(val name: String) : E
data class Bin(val op: String, val l: E, val r: E) : E      // + * < and
data class If(val cond: E, val then: E, val orElse: E) : E
data class Let(val name: String, val value: E, val body: E) : E

class TypeError(message: String) : Exception(message)

fun check(e: E, scope: Map<String, Type>): Type = when (e) {
    is Num -> Type.INT
    is Bool -> Type.BOOL
    is Var -> scope[e.name] ?: throw TypeError("undefined variable '${e.name}'")   // name resolution
    is Let -> check(e.body, scope + (e.name to check(e.value, scope)))            // a new, inner scope
    is Bin -> {
        val l = check(e.l, scope)
        val r = check(e.r, scope)
        val want = if (e.op == "and") Type.BOOL else Type.INT
        if (l != want || r != want) throw TypeError("'${e.op}' needs $want and $want, got $l and $r")
        if (e.op == "<") Type.BOOL else want
    }
    is If -> {
        if (check(e.cond, scope) != Type.BOOL) throw TypeError("'if' needs a BOOL condition")
        val t = check(e.then, scope)
        val f = check(e.orElse, scope)
        if (t != f) throw TypeError("'if' branches differ: $t vs $f")
        t
    }
}

fun main() {
    val programs = listOf(
        "let x = 2 in x * 3 < 7" to Let("x", Num(2), Bin("<", Bin("*", Var("x"), Num(3)), Num(7))),
        "1 + (2 < 3)" to Bin("+", Num(1), Bin("<", Num(2), Num(3))),
        "if 1 then 2 else 3" to If(Num(1), Num(2), Num(3)),
        "if true then 2 else false" to If(Bool(true), Num(2), Bool(false)),
        "let y = 1 in x + y" to Let("y", Num(1), Bin("+", Var("x"), Var("y"))),
    )
    for ((src, tree) in programs) {
        val verdict = try { "ok: " + check(tree, emptyMap()) } catch (e: TypeError) { "error: " + e.message }
        println("%-26s %s".format(src, verdict))
    }
}
