import org.junit.Assert.assertEquals
import org.junit.Test

class PipelineTest {
    @Test fun `cut takes the second comma field`() {
        assertEquals(listOf("42", "7"), cut(listOf("ann,42,x", "bob,7"), ',', 2))
    }

    @Test fun `cut passes a line without the delimiter through, and a missing field is empty`() {
        assertEquals(listOf("nodelim", ""), cut(listOf("nodelim", "a,b"), ',', 5))
    }

    @Test fun `cut sees an empty field between two spaces`() {
        val line = listOf("GET  /books 200")   // two spaces after GET
        assertEquals("field 2", listOf(""), cut(line, ' ', 2))
        assertEquals("field 3", listOf("/books"), cut(line, ' ', 3))
    }

    @Test fun `uniqC merges only adjacent lines`() {
        assertEquals(listOf(2 to "a", 1 to "b", 1 to "a"), uniqC(listOf("a", "a", "b", "a")))
        assertEquals(emptyList<Pair<Int, String>>(), uniqC(emptyList()))
    }

    @Test fun `topN counts every path, not every run`() {
        val paths = listOf("/health", "/books", "/authors", "/books", "/health", "/books")
        assertEquals(listOf(3 to "/books", 2 to "/health"), topN(paths, 2))
    }

    @Test fun `topN keeps equal counts alphabetical and stops when it runs out`() {
        assertEquals(listOf(2 to "/c", 1 to "/a", 1 to "/b"), topN(listOf("/b", "/c", "/a", "/c"), 10))
    }
}
