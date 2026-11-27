package search

import kotlin.math.ln
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexTest {
    // 4 documents; "fox" is in 3 of them, "grapes" and "sour" in 2, "milk" in 1.
    private val index = Index.of(
        mapOf(
            "a" to "The fox and the grapes. Fox!",
            "b" to "A sour fox.",
            "c" to "Sour grapes? Sour milk.",
            "d" to "fox",
        ),
    )
    private val commands = Commands(index)

    @Test
    fun `idf is the smoothed natural log`() {
        assertEquals(ln(5.0 / 4.0) + 1, index.idf("fox"), 1e-12)
        assertEquals(ln(5.0 / 1.0) + 1, index.idf("unknown"), 1e-12)
    }

    @Test
    fun `AND ranks by tf-idf then name`() {
        // idf(fox) = ln(5/4) + 1 = 1.2231, so a scores 2 × 1.2231; b and d tie and go by name.
        assertEquals(
            """
            results: 3
            1. a 2.4463
            2. b 1.2231
            3. d 1.2231
            """.trimIndent(),
            commands.run("q fox"),
        )
        assertEquals("results: 1\n1. a 3.9571", commands.run("q FOX grapes fox"))
        assertEquals("results: 0", commands.run("q fox milk"))
    }

    @Test
    fun `OR scores every matching term`() {
        assertEquals("results: 3\n1. c 4.5325\n2. a 1.5108\n3. b 1.5108", commands.run("or sour grapes"))
    }

    @Test
    fun `stats, doc, prefix and top`() {
        assertEquals(
            """
            documents: 4
            tokens: 10
            terms: 4
            average document length: 2.50
            """.trimIndent(),
            commands.run("stats"),
        )
        assertEquals(
            """
            doc: c
            tokens: 4
            terms: 3
            top terms: sour (2), grapes (1), milk (1)
            """.trimIndent(),
            commands.run("doc c"),
        )
        assertEquals("1. fox 4", commands.run("prefix F"))
        assertEquals("1. fox 4\n2. sour 3", commands.run("top 2"))
        assertEquals("no suggestions", commands.run("prefix z"))
    }

    @Test
    fun `errors are one line`() {
        assertEquals("error: no document named 'zzz'", commands.run("doc zzz"))
        assertEquals("error: no searchable terms in query", commands.run("q the and of"))
        assertEquals("error: usage: top <k> (k is a positive integer)", commands.run("top 0"))
        assertEquals("error: unknown command 'find'", commands.run("find fox"))
    }

    /** Property: AND equals a brute-force scan of every document, on random corpora. */
    @Test
    fun `AND agrees with a scan`() {
        val random = Random(3)
        val vocab = listOf("ant", "bee", "cat", "dog", "eel")
        repeat(200) {
            val texts = (0 until 8).associate { d -> "d$d" to List(6) { vocab.random(random) }.joinToString(" ") }
            val query = listOf(vocab.random(random), vocab.random(random)).distinct()
            val expected = texts.filterValues { tokenize(it).containsAll(query) }.keys
            val actual = Index.of(texts).and(query).map { it.name }.toSet()
            assertEquals(expected, actual, "query $query over $texts")
        }
    }
}
