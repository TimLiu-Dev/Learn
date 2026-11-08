package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// One small test per pipeline stage, written before the stage itself.
class StagesTest {
    private fun texts(src: String) = Lexer(src).tokens().map { it.text }

    @Test fun `scanner groups characters into tokens and skips comments`() {
        assertEquals(listOf("let", "x1", "=", "42", ";", "x1", "/", "2", ""), texts("let x1 = 42; // hi\nx1 / 2"))
    }

    @Test fun `scanner uses maximal munch for names and keywords`() {
        assertEquals(listOf(T.IDENT, T.LET, T.EOF), Lexer("letter let").tokens().map { it.type })
    }

    @Test fun `scanner errors carry line and column`() {
        val e = assertFailsWith<CalcError> { Lexer("1 +\n  @").tokens() }
        assertEquals("unexpected character '@' at 2:3", e.message)
    }

    @Test fun `parser gives precedence and associativity`() {
        assertEquals("(- (- 8 3) (* 2 2))", show(parse("8 - 3 - 2 * 2").result))
        assertEquals("(^ 2 (^ 3 2))", show(parse("2 ^ 3 ^ 2").result))
        assertEquals("(neg (^ 2 2))", show(parse("-2 ^ 2").result))
    }

    @Test fun `parser reports what it expected`() {
        val e = assertFailsWith<CalcError> { parse("let x = (1 + 2; x") }
        assertEquals("expected ')' at 1:15 but found ';'", e.message)
    }

    @Test fun `interpreter evaluates with an environment`() {
        assertEquals(Outcome.Value(24), runInterpreted("let x = 5; let x = x * x; x - 1"))
        assertEquals(Outcome.Value(Int.MIN_VALUE), runInterpreted("2147483647 + 1"))
        assertEquals(Outcome.Error("division by zero at 1:4"), runInterpreted("10 / (5 - 5)"))
    }

    @Test fun `compiler emits the same shape javac does`() {
        val chunk = Compiler().compile(parse("let x = 3; x * (x + 1)"))
        assertEquals(listOf("push", "store", "load", "load", "push", "add", "mul", "return"),
            disassemble(chunk).lines().drop(1).filter { it.isNotBlank() }.map { it.substringAfter(": ").substringBefore(" ") })
        assertEquals(3, chunk.maxStack)
    }

    @Test fun `vm agrees with the interpreter on a long program`() {
        val src = (1..200).joinToString("") { "let v$it = v${it - 1} * 3 + $it % 7;" }.replace("v0", "1") + "v200"
        assertEquals(runInterpreted(src), runCompiled(src))
    }
}
