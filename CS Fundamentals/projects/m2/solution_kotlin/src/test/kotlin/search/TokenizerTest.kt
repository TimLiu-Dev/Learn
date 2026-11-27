package search

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenizerTest {

    @Test
    fun `tokenizes by the spec`() {
        val cases = mapOf(
            "Fox FOX fox" to "fox fox fox",
            "well-known e-mail" to "well known e mail",
            "don't 'tis dogs'" to "don't tis dogs",
            "rock'n'roll x''y" to "rock'n'roll x y",
            "Æsop’s Fables" to "æsop's fables",
            "naïve café ÉCOLE" to "naïve café école",
            "C3PO 3.14 x_y" to "c3po 3 14 x y",
            "The cat and THE hat" to "cat hat",
        )
        for ((text, expected) in cases) assertEquals(expected.split(" "), tokenize(text), "tokenize(\"$text\")")
    }

    @Test
    fun `only separators and stop words give no tokens`() {
        assertEquals(emptyList(), tokenize(" -- ' '' ... The AND of "))
    }

    @Test
    fun `letters outside the BMP are one code point`() {
        // U+1D400 MATHEMATICAL BOLD CAPITAL A is a letter, stored as two chars.
        assertEquals(listOf("𝐀b"), tokenize("𝐀B!"))
    }

    @Test
    fun `stop words are fifty lower-case words`() {
        assertEquals(50, STOP_WORDS.size)
        assertTrue(STOP_WORDS.all { it == it.lowercase() })
    }

    /** Property: on random text, every token is lower-case, well-formed, and re-tokenises to itself. */
    @Test
    fun `random text gives well-formed tokens`() {
        val alphabet = "abcAB'’ -é1.\n"
        val random = Random(42)
        val shape = Regex("[\\p{L}\\p{N}]+('[\\p{L}\\p{N}]+)*")
        repeat(2_000) {
            val text = buildString { repeat(random.nextInt(30)) { append(alphabet[random.nextInt(alphabet.length)]) } }
            val tokens = tokenize(text)
            for (t in tokens) {
                assertTrue(shape.matches(t), "bad token '$t' from: $text")
                assertEquals(t.lowercase(), t, "not lower-case, from: $text")
                assertTrue(t !in STOP_WORDS, "stop word kept, from: $text")
            }
            assertEquals(tokens, tokenize(tokens.joinToString(" ")), "not idempotent: $text")
        }
    }
}
