import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class InterpreterTest {
    private val none = emptyMap<String, Int>()

    @Test fun `children are evaluated before their operator`() {
        // 1 + 2 * 3, and -(8 - 3 - 2) with the tree leaning left
        assertEquals(7, eval(Bin('+', Num(1), Bin('*', Num(2), Num(3))), none))
        assertEquals(-3, eval(Neg(Bin('-', Bin('-', Num(8), Num(3)), Num(2))), none))
    }

    @Test fun `variables come from the environment`() {
        // x * (x + 1) with x = 3
        assertEquals(12, eval(Bin('*', Var("x"), Bin('+', Var("x"), Num(1))), mapOf("x" to 3)))
    }

    @Test fun `a let reads the old value before rebinding`() {
        // let x = 5; let x = x * x; x - 1
        val p = Program(listOf(Let("x", Num(5)), Let("x", Bin('*', Var("x"), Var("x")))), Bin('-', Var("x"), Num(1)))
        assertEquals(24, run(p))
    }

    @Test fun `an undefined variable is a CalcError`() {
        // let a = 1; a + b
        val p = Program(listOf(Let("a", Num(1))), Bin('+', Var("a"), Var("b")))
        assertEquals("undefined variable 'b'", assertThrows(CalcError::class.java) { run(p) }.message)
    }

    @Test fun `division by zero is a CalcError, not a JVM crash`() {
        assertEquals("division by zero", assertThrows(CalcError::class.java) { eval(Bin('/', Num(1), Num(0)), none) }.message)
        assertEquals("division by zero", assertThrows(CalcError::class.java) { eval(Bin('%', Num(1), Num(0)), none) }.message)
    }

    @Test fun `Int arithmetic keeps the JVM's rules`() {
        assertEquals("2147483647 + 1 wraps", Int.MIN_VALUE, eval(Bin('+', Num(Int.MAX_VALUE), Num(1)), none))
        assertEquals("-7 / 2 truncates", -3, eval(Bin('/', Neg(Num(7)), Num(2)), none))
        assertEquals("-7 % 2 takes the dividend's sign", -1, eval(Bin('%', Neg(Num(7)), Num(2)), none))
    }
}
