package katas

import java.util.PriorityQueue

data class Suggestion(val word: String, val count: Int)

// A trie whose word-end nodes carry a count; suggest() returns the k most frequent completions.
class Autocomplete {
    private class Node {
        val children = HashMap<Char, Node>()
        var count = 0                                    // > 0 means a word ends here
    }

    private val root = Node()
    var size = 0                                         // distinct words
        private set

    fun add(word: String, times: Int = 1) {
        require(word.isNotEmpty()) { "word must not be empty" }
        require(times > 0) { "times must be positive" }
        var node = root
        for (c in word.lowercase()) node = node.children.getOrPut(c) { Node() }
        if (node.count == 0) size++
        node.count += times
    }

    // L3's rule for a word: a run of letters a-z, compared case-insensitively.
    fun addText(text: String) {
        for (w in text.lowercase().split(Regex("[^a-z]+"))) if (w.isNotEmpty()) add(w)
    }

    fun suggest(prefix: String, k: Int): List<Suggestion> {
        require(k > 0) { "k must be positive" }
        val p = prefix.lowercase()
        var node = root
        for (c in p) node = node.children[c] ?: return emptyList()

        // L20's top-k: a min-heap holding the best k so far, with the weakest at the root.
        val weakestFirst = compareBy<Suggestion> { it.count }.thenByDescending { it.word }
        val heap = PriorityQueue(weakestFirst)
        fun collect(n: Node, sb: StringBuilder) {
            if (n.count > 0) {
                heap.add(Suggestion(sb.toString(), n.count))
                if (heap.size > k) heap.poll()           // drop the weakest: O(log k)
            }
            for ((c, child) in n.children) {
                sb.append(c); collect(child, sb); sb.setLength(sb.length - 1)
            }
        }
        collect(node, StringBuilder(p))
        return heap.sortedWith(weakestFirst.reversed())  // most frequent first, ties A to Z
    }
}
