import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Part 1's divisible-by-3 machine, written as a table
val div3 = Dfa(listOf(mapOf('0' to 0, '1' to 1), mapOf('0' to 2, '1' to 0), mapOf('0' to 1, '1' to 2)), setOf(0))

class DfaTest {
    @Test fun `the trace of 110 includes the start state`() {
        assertEquals(listOf(0, 1, 0, 0), div3.trace("110"))
        assertTrue("6 is divisible by 3", div3.accepts("110"))
        assertFalse("7 is not", div3.accepts("111"))
    }

    @Test fun `the empty input stays in the start state`() {
        assertEquals(listOf(0), div3.trace(""))
        assertFalse(Dfa(listOf(mapOf('a' to 0)), setOf(1)).accepts(""))
    }

    @Test fun `a character with no move rejects`() {
        assertNull(div3.trace("12"))
        assertFalse(div3.accepts("12"))
    }

    @Test fun `divisibleBy(m) has one state per remainder`() {
        assertEquals(7, divisibleBy(7).table.size)
    }

    @Test fun `divisibleBy(5) agrees with arithmetic below 1024`() {
        val dfa = divisibleBy(5)
        for (v in 0 until 1024) {
            val bits = Integer.toBinaryString(v)
            assertEquals("for $bits ($v)", v % 5 == 0, dfa.accepts(bits))
        }
    }
}
