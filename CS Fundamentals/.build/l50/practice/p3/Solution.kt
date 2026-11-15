class PrefixTable {
    private class Node {
        val children = HashMap<Char, Node>()
        var isWord = false
    }

    private val root = Node()

    fun insert(word: String) {
        var node = root
        for (c in word) node = node.children.getOrPut(c) { Node() }
        node.isWord = true
    }

    // The longest inserted word that is a prefix of query, or null.
    fun longestPrefixOf(query: String): String? {
        var node = root
        var bestLen = if (root.isWord) 0 else -1         // -1: no word end passed yet
        for ((i, c) in query.withIndex()) {
            node = node.children[c] ?: break             // fell off: the answer is the last word end
            if (node.isWord) bestLen = i + 1
        }
        return if (bestLen < 0) null else query.substring(0, bestLen)
    }
}
