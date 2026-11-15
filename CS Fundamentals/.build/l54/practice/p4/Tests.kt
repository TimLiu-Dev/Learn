import org.junit.Assert.assertEquals
import org.junit.Test

// The tree-walking interpreter, used as the reference: the compiled code must agree with it.
fun value(e: Expr): Int = when (e) {
    is Num -> e.value
    is Neg -> -value(e.operand)
    is Bin -> when (e.op) {
        '+' -> value(e.left) + value(e.right)
        '-' -> value(e.left) - value(e.right)
        '*' -> value(e.left) * value(e.right)
        else -> value(e.left) / value(e.right)
    }
}

class StackMachineTest {
    private val onePlusTwoTimesThree = Bin('+', Num(1), Bin('*', Num(2), Num(3)))
    private val eightMinusThreeMinusTwo = Bin('-', Bin('-', Num(8), Num(3)), Num(2))

    @Test fun `operands are emitted before their operator`() {
        assertEquals(listOf("push 1", "push 2", "push 3", "mul", "add"), compile(onePlusTwoTimesThree))
        assertEquals(listOf("push 8", "push 3", "sub", "push 2", "sub"), compile(eightMinusThreeMinusTwo))
    }

    @Test fun `negation comes after its operand`() {
        assertEquals(listOf("push 4", "neg"), compile(Neg(Num(4))))
    }

    @Test fun `the machine runs hand-written code`() {
        assertEquals("1 + 2 * 3", 7, execute(listOf("push 1", "push 2", "push 3", "mul", "add")))
        assertEquals("(1 + 2) * 3", 9, execute(listOf("push 1", "push 2", "add", "push 3", "mul")))
    }

    @Test fun `operand order matters for sub and div`() {
        assertEquals("8 - 3", 5, execute(listOf("push 8", "push 3", "sub")))
        assertEquals("20 / 4", 5, execute(listOf("push 20", "push 4", "div")))
    }

    @Test fun `compiled code agrees with the interpreter`() {
        val trees = listOf(
            onePlusTwoTimesThree,
            eightMinusThreeMinusTwo,
            Bin('-', Num(8), Bin('-', Num(3), Num(2))),
            Bin('/', Neg(Bin('*', Num(7), Num(6))), Bin('-', Num(10), Num(3))),
        )
        for (t in trees) assertEquals(t.toString(), value(t), execute(compile(t)))
    }
}
