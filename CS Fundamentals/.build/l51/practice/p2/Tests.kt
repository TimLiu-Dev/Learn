import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NfaTest {
    private val aqaq = coxNfa(2)                   // a?a?aa, the NFA drawn in Part 2

    @Test fun `ε-moves chain, so the start set of a?a?aa is 0, 1 and 2`() {
        assertEquals(setOf(0, 1, 2), closure(aqaq, setOf(0)))
    }

    @Test fun `closure keeps the states it starts from`() {
        assertEquals(setOf(3), closure(aqaq, setOf(3)))
    }

    @Test fun `one step reads a from every state in the set`() {
        assertEquals(setOf(1, 2, 3), step(aqaq, setOf(0, 1, 2), 'a'))
        assertEquals("no move on b", emptySet<Int>(), step(aqaq, setOf(0, 1, 2), 'b'))
    }

    @Test fun `a?a?aa matches two to four a's and nothing else`() {
        val inputs = listOf("", "a", "aa", "aaa", "aaaa", "aaaaa", "ab")
        assertEquals(listOf(false, false, true, true, true, false, false), inputs.map { matches(aqaq, it) })
    }

    @Test fun `an ε-cycle does not loop forever`() {
        val star = Nfa(0, 1, listOf(Move(1, 'a', 0)), listOf(0 to 1, 1 to 0))   // a*, with an ε-cycle 0 ⇄ 1
        assertEquals(setOf(0, 1), closure(star, setOf(0)))
        assertTrue(matches(star, ""))
        assertTrue(matches(star, "aaa"))
        assertFalse(matches(star, "ab"))
    }

    @Test fun `a?{40}a{40} is no problem for state sets`() {
        val nfa = coxNfa(40)
        assertTrue(matches(nfa, "a".repeat(40)))
        assertFalse(matches(nfa, "a".repeat(81)))
    }
}
