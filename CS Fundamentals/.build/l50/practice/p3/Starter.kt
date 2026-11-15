class PrefixTable {
    private class Node {
        val children = HashMap<Char, Node>()
        var isWord = false
    }

    private val root = Node()

    fun insert(word: String): Unit = TODO("walk down, creating missing nodes; mark the last one as a word end")

    // The longest inserted word that is a prefix of query, or null.
    fun longestPrefixOf(query: String): String? = TODO("walk down the query, remembering the last word end")
}
