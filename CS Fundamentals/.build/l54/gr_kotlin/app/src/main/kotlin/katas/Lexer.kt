package katas

// Calc's scanner: source text -> a list of tokens, each remembering where it came from.
enum class T { NUMBER, IDENT, LET, PLUS, MINUS, STAR, SLASH, PERCENT, CARET, LPAREN, RPAREN, EQUAL, SEMI, EOF }

data class Token(val type: T, val text: String, val line: Int, val col: Int) {
    val at get() = "$line:$col"
    override fun toString() = "$type '$text' $at"
}

class CalcError(message: String) : RuntimeException(message)

class Lexer(private val src: String) {
    private var i = 0
    private var line = 1
    private var col = 1

    fun tokens(): List<Token> = buildList {
        while (true) {
            skipSpaceAndComments()
            if (i == src.length) {
                add(Token(T.EOF, "", line, col))
                return@buildList
            }
            add(next())
        }
    }

    private fun next(): Token {
        val (startLine, startCol, start) = Triple(line, col, i)
        val c = advance()
        fun token(type: T) = Token(type, src.substring(start, i), startLine, startCol)
        return when (c) {
            in '0'..'9' -> {
                while (peek() in '0'..'9') advance()
                val t = token(T.NUMBER)
                t.text.toIntOrNull() ?: throw CalcError("number too large '${t.text}' at ${t.at}")
                t
            }
            in 'a'..'z', in 'A'..'Z', '_' -> {
                while (peek().isWordChar()) advance()
                token(if (src.substring(start, i) == "let") T.LET else T.IDENT)
            }
            '+' -> token(T.PLUS)
            '-' -> token(T.MINUS)
            '*' -> token(T.STAR)
            '/' -> token(T.SLASH)          // "//" never gets here: skipSpaceAndComments ate it
            '%' -> token(T.PERCENT)
            '^' -> token(T.CARET)
            '(' -> token(T.LPAREN)
            ')' -> token(T.RPAREN)
            '=' -> token(T.EQUAL)
            ';' -> token(T.SEMI)
            else -> throw CalcError("unexpected character '$c' at $startLine:$startCol")
        }
    }

    private fun skipSpaceAndComments() {
        while (i < src.length) {
            when {
                peek() == ' ' || peek() == '\t' || peek() == '\r' || peek() == '\n' -> advance()
                peek() == '/' && peek(1) == '/' -> while (i < src.length && peek() != '\n') advance()
                else -> return
            }
        }
    }

    private fun Char.isWordChar() = this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9' || this == '_'
    private fun peek(ahead: Int = 0) = if (i + ahead < src.length) src[i + ahead] else '\u0000'
    private fun advance(): Char {
        val c = src[i++]
        if (c == '\n') { line++; col = 1 } else col++
        return c
    }
}
