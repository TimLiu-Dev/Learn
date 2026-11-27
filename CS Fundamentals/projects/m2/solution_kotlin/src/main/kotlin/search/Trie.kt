package search

/** A term with a count: corpus frequency for prefix and top, term frequency for doc. */
data class TermCount(val term: String, val count: Long)

/** Most frequent first, ties alphabetical: the order for prefix, top and doc. */
val BY_COUNT: Comparator<TermCount> = compareByDescending<TermCount> { it.count }.thenBy { it.term }

/** A trie of terms, each word-end node carrying the term's corpus frequency (L50). */
class Trie {
    private class Node {
        val children = HashMap<Char, Node>()
        var count = 0L // 0 means "no term ends here"
    }

    private val root = Node()

    fun add(term: String, count: Long) {
        require(count >= 1) { "count must be positive: $term" }
        var node = root
        for (ch in term) node = node.children.getOrPut(ch) { Node() }
        node.count += count
    }

    /** Every term that starts with [prefix], including the prefix itself if it is a term. */
    fun withPrefix(prefix: String): List<TermCount> {
        var node: Node = root
        for (ch in prefix) node = node.children[ch] ?: return emptyList()
        val out = mutableListOf<TermCount>()
        collect(node, StringBuilder(prefix), out)
        return out
    }

    private fun collect(node: Node, path: StringBuilder, out: MutableList<TermCount>) {
        if (node.count > 0) out += TermCount(path.toString(), node.count)
        for ((ch, child) in node.children) {
            path.append(ch)
            collect(child, path, out)
            path.setLength(path.length - 1)
        }
    }
}
