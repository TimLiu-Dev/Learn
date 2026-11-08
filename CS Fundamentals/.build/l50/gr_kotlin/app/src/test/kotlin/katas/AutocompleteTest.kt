package katas

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AutocompleteTest {
    private fun of(vararg pairs: Pair<String, Int>) = Autocomplete().apply { for ((w, n) in pairs) add(w, n) }

    @Test fun `empty autocomplete suggests nothing`() {
        assertTrue(Autocomplete().suggest("a", 3).isEmpty())
    }

    @Test fun `unknown prefix gives an empty list`() {
        assertTrue(of("cat" to 1).suggest("dog", 3).isEmpty())
    }

    @Test fun `ranks by count, most frequent first`() {
        val ac = of("tea" to 2, "ten" to 9, "to" to 5)
        assertEquals(listOf("ten", "to", "tea"), ac.suggest("t", 5).map { it.word })
    }

    @Test fun `ties are broken alphabetically`() {
        val ac = of("bat" to 3, "ball" to 3, "bad" to 3)
        assertEquals(listOf("bad", "ball", "bat"), ac.suggest("ba", 5).map { it.word })
    }

    @Test fun `k limits the number of suggestions`() {
        val ac = of("a" to 1, "ab" to 2, "abc" to 3, "abd" to 4)
        assertEquals(listOf(Suggestion("abd", 4), Suggestion("abc", 3)), ac.suggest("a", 2))
    }

    @Test fun `a prefix that is itself a word is included`() {
        val ac = of("car" to 1, "cart" to 2)
        assertEquals(listOf("cart", "car"), ac.suggest("car", 5).map { it.word })
    }

    @Test fun `addText counts words case-insensitively`() {
        val ac = Autocomplete().apply { addText("The cat. THE hat, the mat!") }
        assertEquals(Suggestion("the", 3), ac.suggest("th", 1).single())
        assertEquals(4, ac.size)
    }

    @Test fun `empty prefix ranks every word`() {
        val ac = of("x" to 1, "y" to 3, "z" to 2)
        assertEquals(listOf("y", "z", "x"), ac.suggest("", 10).map { it.word })
    }

    @Test fun `k must be positive`() {
        assertFailsWith<IllegalArgumentException> { of("a" to 1).suggest("a", 0) }
    }

    @Test fun `matches a brute-force scan on random dictionaries`() {
        val rnd = Random(50)
        repeat(500) {
            val counts = HashMap<String, Int>()
            val ac = Autocomplete()
            repeat(rnd.nextInt(1, 40)) {
                val w = String(CharArray(rnd.nextInt(1, 5)) { "abc"[rnd.nextInt(3)] })   // tiny alphabet: many shared prefixes
                val n = rnd.nextInt(1, 4)
                ac.add(w, n)
                counts[w] = (counts[w] ?: 0) + n
            }
            val p = String(CharArray(rnd.nextInt(0, 3)) { "abc"[rnd.nextInt(3)] })
            val k = rnd.nextInt(1, 6)
            val want = counts.filterKeys { it.startsWith(p) }.map { Suggestion(it.key, it.value) }
                .sortedWith(compareByDescending<Suggestion> { it.count }.thenBy { it.word }).take(k)
            assertEquals(want, ac.suggest(p, k), "prefix '$p', k=$k, counts=$counts")
        }
    }
}
