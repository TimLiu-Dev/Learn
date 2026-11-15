sealed interface Expr
data class Num(val value: Int) : Expr
data class Bin(val op: Char, val left: Expr, val right: Expr) : Expr

class CalcError(message: String) : RuntimeException(message)

fun show(e: Expr): String = when (e) {
    is Num -> e.value.toString()
    is Bin -> "(${e.op} ${show(e.left)} ${show(e.right)})"
}

// Tokens are regular, so one regex scans them: numbers, operators, and any other single character.
fun tokenize(src: String): List<String> = Regex("""\d+|\S""").findAll(src).map { it.value }.toList()

fun parse(src: String): Expr = Parser(tokenize(src)).parse()

//   expr := term ( ( "+" | "-" ) term )*
//   term := atom ( ( "*" | "/" ) atom )*
//   atom := NUMBER | "(" expr ")"
class Parser(private val tokens: List<String>) {
    private var pos = 0

    fun parse(): Expr {
        val e = expr()
        if (pos < tokens.size) throw CalcError("unexpected '${tokens[pos]}' at token ${pos + 1}")
        return e
    }

    private fun expr(): Expr {
        val left = term()
        if (peek() == "+" || peek() == "-") {
            val op = next()!![0]
            return Bin(op, left, expr())      // recursion on the right
        }
        return left
    }

    private fun term(): Expr {
        val left = atom()
        if (peek() == "*" || peek() == "/") {
            val op = next()!![0]
            return Bin(op, left, term())
        }
        return left
    }

private fun atom(): Expr {
        val t = next() ?: throw CalcError("expected a number or '(' but found end of input")
        return when {
            t == "(" -> expr().also { if (next() != ")") throw CalcError("expected ')'") }
            t[0] in '0'..'9' -> Num(t.toInt())
            else -> throw CalcError("expected a number or '(' but found '$t'")
        }
    }

    private fun peek(): String? = tokens.getOrNull(pos)
    private fun next(): String? = tokens.getOrNull(pos++)
}
