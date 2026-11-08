import java.util.TreeMap

// A trie: one node per distinct prefix. The path from the root spells the prefix;
// isWord marks the nodes where a whole word ends.
class Trie {
    private class Node {
        val children = TreeMap<Char, Node>()             // sorted, so words come out in order
        var isWord = false
    }

    private val root = Node()
    var nodes = 1
        private set

    fun insert(word: String) {
        var node = root
        for (c in word) {
            node = node.children.getOrPut(c) { nodes++; Node() }
        }
        node.isWord = true
    }

    private fun find(prefix: String): Node? {
        var node = root
        for (c in prefix) node = node.children[c] ?: return null   // fell off the trie
        return node
    }

    fun contains(word: String) = find(word)?.isWord == true
    fun hasPrefix(prefix: String) = find(prefix) != null

    fun withPrefix(prefix: String): List<String> {
        val out = mutableListOf<String>()
        fun collect(node: Node, sb: StringBuilder) {       // DFS below the prefix node (L20 pre-order)
            if (node.isWord) out += sb.toString()
            for ((c, child) in node.children) {
                sb.append(c); collect(child, sb); sb.setLength(sb.length - 1)
            }
        }
        find(prefix)?.let { collect(it, StringBuilder(prefix)) }
        return out
    }
}

fun main() {
    val t = Trie()
    val words = "she sells sea shells by the sea shore".split(" ")
    words.forEach(t::insert)
    println("${words.toSet().size} distinct words, ${words.toSet().sumOf { it.length }} letters, ${t.nodes} nodes")
    println("contains sea: ${t.contains("sea")}   contains se: ${t.contains("se")}   hasPrefix se: ${t.hasPrefix("se")}")
    println("withPrefix sh: ${t.withPrefix("sh")}")
    println("withPrefix s:  ${t.withPrefix("s")}")
    println("withPrefix x:  ${t.withPrefix("x")}")
}
