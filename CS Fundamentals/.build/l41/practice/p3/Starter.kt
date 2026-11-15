// An LRU cache bounded by total weight: the sum of its values' lengths.
class WeightedLru(private val maxWeight: Int) {
    private class Node(val key: String?, var value: String) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val map = HashMap<String, Node>()
    private val head = Node(null, "")                 // most recently used side
    private val tail = Node(null, "")                 // least recently used side
    var totalWeight = 0
        private set
    val evicted = mutableListOf<String>()             // keys pushed out to make room, in order

    init { head.next = tail; tail.prev = head }

    fun get(key: String): String? {
        TODO("find the node, move it to the front, return its value")
    }

    fun put(key: String, value: String) {
        TODO("update or insert, keep totalWeight right, then evict from the back while too heavy")
    }

    // Keys, most recently used first.
    fun keys(): List<String> =
        generateSequence(head.next) { it.next }.takeWhile { it !== tail }.map { it.key!! }.toList()

    private fun unlink(n: Node) { n.prev!!.next = n.next; n.next!!.prev = n.prev }
    private fun addFront(n: Node) { n.next = head.next; n.prev = head; head.next!!.prev = n; head.next = n }
}
