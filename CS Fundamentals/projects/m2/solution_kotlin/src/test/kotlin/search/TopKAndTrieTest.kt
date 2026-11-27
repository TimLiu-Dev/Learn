package search

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TopKAndTrieTest {

    @Test
    fun `top k keeps the best in order`() {
        assertEquals(listOf(9, 8, 7), listOf(3, 9, 1, 7, 8, 2).topK(3, reverseOrder()))
        assertEquals(listOf(2, 1), listOf(1, 2).topK(5, reverseOrder()))
        assertFailsWith<IllegalArgumentException> { listOf(1).topK(0, naturalOrder()) }
    }

    /** Property: the heap agrees with "sort everything, take k" on random inputs with many ties. */
    @Test
    fun `top k matches a full sort`() {
        val random = Random(7)
        repeat(1_000) {
            val items = List(random.nextInt(40)) { i -> TermCount("w$i", 1L + random.nextInt(4)) }
            val k = 1 + random.nextInt(8)
            assertEquals(items.sortedWith(BY_COUNT).take(k), items.topK(k, BY_COUNT))
        }
    }

    @Test
    fun `prefix includes the word itself but not inner nodes`() {
        val trie = Trie()
        trie.add("car", 2)
        trie.add("card", 5)
        trie.add("cart", 2)
        assertEquals(listOf("card", "car", "cart"), trie.withPrefix("car").sortedWith(BY_COUNT).map { it.term })
        assertEquals(3, trie.withPrefix("ca").size, "'ca' is a path, not a word")
        assertEquals(emptyList(), trie.withPrefix("cars"))
    }

    /** Property: the trie finds exactly what a brute-force filter over all words finds. */
    @Test
    fun `trie matches brute force`() {
        val random = Random(11)
        fun word() = String(CharArray(1 + random.nextInt(4)) { 'a' + random.nextInt(3) })
        repeat(300) {
            val trie = Trie()
            val words = HashMap<String, Long>()
            repeat(30) {
                val w = word()
                val c = 1L + random.nextInt(3)
                trie.add(w, c)
                words.merge(w, c, Long::plus)
            }
            val prefix = word()
            val expected = words.filterKeys { it.startsWith(prefix) }.map { TermCount(it.key, it.value) }.sortedWith(BY_COUNT)
            assertEquals(expected, trie.withPrefix(prefix).sortedWith(BY_COUNT))
        }
    }
}
