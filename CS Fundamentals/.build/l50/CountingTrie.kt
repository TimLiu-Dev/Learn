// Count the words that start with a prefix in O(prefix length), without visiting them:
// every node remembers how many inserted words pass through it.
class CountingTrie {
    private class Node {
        val children = HashMap<Char, Node>()
        var pass = 0                                     // words that go through this node
        var isWord = false
    }

    private val root = Node()

    fun insert(word: String) {
        if (contains(word)) return                       // a set: count each word once
        var node = root
        node.pass++
        for (c in word) {
            node = node.children.getOrPut(c) { Node() }
            node.pass++
        }
        node.isWord = true
    }

    private fun find(prefix: String): Node? {
        var node = root
        for (c in prefix) node = node.children[c] ?: return null
        return node
    }

    fun contains(word: String) = find(word)?.isWord == true
    fun countPrefix(prefix: String) = find(prefix)?.pass ?: 0
}

fun main() {
    val t = CountingTrie()
    "she sells sea shells by the sea shore".split(" ").forEach(t::insert)
    for (p in listOf("", "s", "sh", "she", "shel", "x")) println("countPrefix(\"$p\") = ${t.countPrefix(p)}")
}
