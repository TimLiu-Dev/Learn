package search

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Examples of the testing style. Add your own for every rule in the spec. */
class TokenizerTest {

    @Test
    fun `stop words are fifty lower-case words`() {
        assertEquals(50, STOP_WORDS.size)
        assertTrue(STOP_WORDS.all { it == it.lowercase() })
    }

    @Test
    fun `lower-cases, splits and drops stop words`() {
        assertEquals(listOf("fox", "jumped", "over", "lazy", "dog"), tokenize("The Fox jumped over-the LAZY dog!"))
    }

    /** A property test: whatever the text, no token is empty, upper-case or a stop word. */
    @Test
    fun `random text gives clean tokens`() {
        val alphabet = "abcAB -.!\n"
        val random = Random(42) // seeded, so a failure always reproduces
        repeat(1_000) {
            val text = buildString { repeat(random.nextInt(30)) { append(alphabet[random.nextInt(alphabet.length)]) } }
            for (t in tokenize(text)) {
                assertTrue(t.isNotEmpty() && t == t.lowercase() && t !in STOP_WORDS, "bad token '$t' from: $text")
            }
        }
    }
}
