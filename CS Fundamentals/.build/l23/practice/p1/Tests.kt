import org.junit.Assert.assertEquals
import org.junit.Test

class InterpreterTest {
    @Test fun `add(2, 3) is 5`() {
        val add = listOf("iload 0", "iload 1", "iadd", "ireturn")
        assertEquals(5, execute(add, 2, 3))
    }

    @Test fun `mul3(2, 3, 4) is 24, as in the Part 1 exercise`() {
        val mul3 = listOf("iload 0", "iload 1", "imul", "iload 2", "imul", "ireturn")
        assertEquals(24, execute(mul3, 2, 3, 4))
    }

    @Test fun `isub takes the first-pushed value minus the last-pushed`() {
        val sub = listOf("iload 0", "iload 1", "isub", "ireturn")
        assertEquals("sub(10, 3) must be 10 - 3", 7, execute(sub, 10, 3))
    }

    @Test fun `istore and iload round-trip through a local slot`() {
        // int t = a * b; return t - 1;   (javac: iload_0 iload_1 imul istore_2 iload_2 iconst_1 isub ireturn)
        val code = listOf("iload 0", "iload 1", "imul", "istore 2", "iload 2", "iconst 1", "isub", "ireturn")
        assertEquals(41, execute(code, 6, 7))
    }

    @Test fun `ints wrap around like the JVM's`() {
        val add = listOf("iload 0", "iconst 1", "iadd", "ireturn")
        assertEquals(Int.MIN_VALUE, execute(add, Int.MAX_VALUE))
    }
}
