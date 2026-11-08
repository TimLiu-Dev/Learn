// Calc's syntax tree and its recursive-descent parser: one function per grammar rule.
//   program := ( "let" IDENT "=" expr ";" )* expr EOF
//   expr    := term ( ( "+" | "-" ) term )*
//   term    := unary ( ( "*" | "/" | "%" ) unary )*
//   unary   := "-" unary | power
//   power   := atom ( "^" unary )?
//   atom    := NUMBER | IDENT | "(" expr ")"
sealed interface Expr
data class Num(val value: Int) : Expr
data class Var(val name: String, val at: Token) : Expr
data class Neg(val operand: Expr, val at: Token) : Expr
data class Bin(val op: T, val left: Expr, val right: Expr, val at: Token) : Expr

data class Let(val name: String, val value: Expr)
data class Program(val lets: List<Let>, val result: Expr)

class Parser(private val tokens: List<Token>) {
    private var pos = 0

    fun program(): Program {
        val lets = mutableListOf<Let>()
        while (peek().type == T.LET) {
            advance()
            val name = expect(T.IDENT, "a variable name").text
            expect(T.EQUAL, "'='")
            val value = expr()
            expect(T.SEMI, "';'")
            lets += Let(name, value)
        }
        val result = expr()
        expect(T.EOF, "end of input")
        return Program(lets, result)
    }

    private fun expr(): Expr {
        var left = term()
        while (peek().type == T.PLUS || peek().type == T.MINUS) {
            val op = advance()
            left = Bin(op.type, left, term(), op)      // loop, not recursion: left-associative
        }
        return left
    }

    private fun term(): Expr {
        var left = unary()
        while (peek().type == T.STAR || peek().type == T.SLASH || peek().type == T.PERCENT) {
            val op = advance()
            left = Bin(op.type, left, unary(), op)
        }
        return left
    }

    private fun unary(): Expr =
        if (peek().type == T.MINUS) { val op = advance(); Neg(unary(), op) } else power()

    private fun power(): Expr {
        val base = atom()
        if (peek().type != T.CARET) return base
        val op = advance()
        return Bin(T.CARET, base, unary(), op)         // recursion on the right: right-associative
    }

    private fun atom(): Expr {
        val t = advance()
        return when (t.type) {
            T.NUMBER -> Num(t.text.toInt())
            T.IDENT -> Var(t.text, t)
            T.LPAREN -> expr().also { expect(T.RPAREN, "')'") }
            else -> throw error(t, "a number, a name or '('")
        }
    }

    private fun peek() = tokens[pos]
    private fun advance() = tokens[pos].also { if (it.type != T.EOF) pos++ }
    private fun expect(type: T, what: String): Token =
        if (peek().type == type) advance() else throw error(peek(), what)
    private fun error(t: Token, what: String) =
        CalcError("expected $what at ${t.at} but found ${if (t.type == T.EOF) "end of input" else "'${t.text}'"}")
}

fun parse(src: String): Program = Parser(Lexer(src).tokens()).program()

// Lisp-style printer: every operator wraps its operands, so the tree's shape is visible.
fun show(e: Expr): String = when (e) {
    is Num -> e.value.toString()
    is Var -> e.name
    is Neg -> "(neg ${show(e.operand)})"
    is Bin -> "(${e.at.text} ${show(e.left)} ${show(e.right)})"
}

fun show(p: Program): String =
    (p.lets.map { "let ${it.name} = ${show(it.value)}" } + show(p.result)).joinToString("\n")
