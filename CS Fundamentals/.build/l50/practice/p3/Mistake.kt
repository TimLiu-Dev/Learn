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

    fun longestPrefixOf(query: String): String? {
        var node = root
        var bestLen = -1
        for ((i, c) in query.withIndex()) {
            node = node.children[c] ?: return null       // copied from find(): forgets what it passed
            if (node.isWord) bestLen = i + 1
        }
        return if (bestLen < 0) null else query.substring(0, bestLen)
    }
}
