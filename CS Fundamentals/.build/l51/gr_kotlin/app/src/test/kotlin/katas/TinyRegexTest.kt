package katas

import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class TinyRegexTest {
    private fun m(pattern: String, input: String) = TinyRegex(pattern).matches(input)

    @Test fun `a literal must match the whole input`() {
        assertTrue(m("abc", "abc"))
        assertFalse(m("abc", "ab"))
        assertFalse(m("abc", "abcd"))
    }

    @Test fun `dot matches exactly one character`() {
        assertTrue(m("a.c", "abc"))
        assertTrue(m("a.c", "a.c"))
        assertFalse(m("a.c", "ac"))
    }

    @Test fun `star plus and question mark`() {
        assertTrue(m("ab*", "a")); assertTrue(m("ab*", "abbb"))
        assertFalse(m("ab+", "a")); assertTrue(m("ab+", "abb"))
        assertTrue(m("ab?c", "ac")); assertTrue(m("ab?c", "abc")); assertFalse(m("ab?c", "abbc"))
    }

    @Test fun `alternation has the lowest precedence and parentheses group`() {
        assertTrue(m("ab|cd", "ab")); assertTrue(m("ab|cd", "cd")); assertFalse(m("ab|cd", "abd"))
        assertTrue(m("a(b|c)d", "acd")); assertFalse(m("a(b|c)d", "ab"))
        assertTrue(m("(ab)*", "ababab")); assertFalse(m("(ab)*", "aba"))
    }

    @Test fun `empty pattern and empty branches match the empty string`() {
        assertTrue(m("", "")); assertFalse(m("", "a"))
        assertTrue(m("a|", "")); assertTrue(m("a|", "a"))
        assertTrue(m("()", ""))
    }

    @Test fun `a backslash makes a metacharacter literal`() {
        assertTrue(m("""a\.b""", "a.b"))
        assertFalse(m("""a\.b""", "axb"))
        assertTrue(m("""\(\*\)""", "(*)"))
    }

    @Test fun `malformed patterns are rejected`() {
        for (bad in listOf("(ab", "ab)", "*a", "a**", "a*?", "a\\", "[ab]", "a{2}")) {
            assertFailsWith<IllegalArgumentException>(bad) { TinyRegex(bad) }
        }
    }

    @Test fun `agrees with java util regex on thousands of random cases`() {
        val rnd = Random(51)
        fun gen(depth: Int): String = when (if (depth == 0) rnd.nextInt(3) else rnd.nextInt(7)) {
            0 -> "a"
            1 -> "b"
            2 -> "."
            3 -> gen(depth - 1) + gen(depth - 1)
            4 -> gen(depth - 1) + "|" + gen(depth - 1)
            5 -> "(" + gen(depth - 1) + ")" + "*+?"[rnd.nextInt(3)]
            else -> "(" + gen(depth - 1) + ")"
        }
        var cases = 0
        repeat(2_000) {
            val pattern = gen(4)
            val ours = TinyRegex(pattern)
            val theirs = Regex(pattern)
            repeat(25) {
                val input = (0 until rnd.nextInt(9)).map { "ab"[rnd.nextInt(2)] }.joinToString("")
                assertEquals(theirs.matches(input), ours.matches(input), "pattern $pattern, input \"$input\"")
                cases++
            }
        }
        assertEquals(50_000, cases)
    }

    @Test fun `patterns that defeat backtracking stay fast`() {
        val t = measureTime {
            assertFalse(m("(a+)+b", "a".repeat(100_000) + "c"))
            assertTrue(m("a?".repeat(30) + "a".repeat(30), "a".repeat(30)))
            assertFalse(m("(.*a)".repeat(12), "a".repeat(28) + "x"))
        }
        assertTrue(t < 1.seconds, "took $t")
    }

    @Test fun `deeply nested stars do not overflow the stack`() {
        assertTrue(m("((a*)*)*", "a".repeat(50_000)))
    }
}
