import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

// A tiny evaluator, so each check can show the value as well as the tree.
fun value(e: Expr): Int = when (e) {
    is Num -> e.value
    is Bin -> when (e.op) {
        '+' -> value(e.left) + value(e.right)
        '-' -> value(e.left) - value(e.right)
        '*' -> value(e.left) * value(e.right)
        else -> value(e.left) / value(e.right)
    }
}

class ParserTest {
    @Test fun `multiplication binds tighter than addition`() {
        assertEquals("(+ 1 (* 2 3))", show(parse("1+2*3")))
        assertEquals("1+2*3", 7, value(parse("1+2*3")))
    }

    @Test fun `parentheses override precedence`() {
        assertEquals("(* (+ 1 2) 3)", show(parse("(1+2)*3")))
        assertEquals("(1+2)*3", 9, value(parse("(1+2)*3")))
    }

    @Test fun `subtraction is left-associative`() {
        assertEquals("(- (- 8 3) 2)", show(parse("8 - 3 - 2")))
        assertEquals("8-3-2", 3, value(parse("8 - 3 - 2")))
    }

    @Test fun `division is left-associative too`() {
        assertEquals("100/10/5", 2, value(parse("100 / 10 / 5")))
    }

    @Test fun `a longer mix`() {
        assertEquals("(- (+ (* 2 3) (* 4 5)) 6)", show(parse("2*3 + 4*5 - 6")))
    }

    @Test fun `errors say what was expected`() {
        assertEquals("expected a number or '(' but found end of input",
            assertThrows(CalcError::class.java) { parse("1 +") }.message)
        assertEquals("unexpected '2' at token 2",
            assertThrows(CalcError::class.java) { parse("1 2") }.message)
    }
}
