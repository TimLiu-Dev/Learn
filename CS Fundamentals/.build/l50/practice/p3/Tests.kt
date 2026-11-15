import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrefixTableTest {
    private fun table(vararg words: String) = PrefixTable().apply { words.forEach { insert(it) } }

    @Test fun `a stored word is its own longest prefix`() {
        assertEquals("shells", table("she", "shells").longestPrefixOf("shells"))
    }

    @Test fun `falling off the trie keeps the last word end passed`() {
        assertEquals("she", table("she", "shells").longestPrefixOf("shelf"))
    }

    @Test fun `a longer query still finds the longest word`() {
        assertEquals("shells", table("she", "shells").longestPrefixOf("shellsfish"))
    }

    @Test fun `a path with no word end gives null`() {
        val t = table("sea")
        assertNull("se is a path, not a word", t.longestPrefixOf("se"))
        assertEquals("sea", t.longestPrefixOf("seat"))
    }

    @Test fun `a query outside the trie gives null`() {
        assertNull(table("she", "sea").longestPrefixOf("xyz"))
        assertNull(table("she").longestPrefixOf(""))
    }

    @Test fun `the most specific route wins`() {
        val routes = table("128.112", "128.112.136")
        assertEquals("128.112.136", routes.longestPrefixOf("128.112.136.12"))
        assertEquals("128.112", routes.longestPrefixOf("128.112.5.1"))
        assertNull(routes.longestPrefixOf("10.0.0.1"))
    }
}
