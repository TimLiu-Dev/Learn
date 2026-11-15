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
        val n = map[key] ?: return null
        unlink(n); addFront(n)
        return n.value
    }

    fun put(key: String, value: String) {
        val old = map[key]
        if (value.length > maxWeight) {                // can never fit: drop the stale copy too
            if (old != null) { unlink(old); map.remove(key); totalWeight -= old.value.length }
            return
        }
        if (old != null) {
            totalWeight += value.length - old.value.length
            old.value = value
            unlink(old); addFront(old)
        } else {
            val n = Node(key, value)
            map[key] = n; addFront(n)
            totalWeight += value.length
        }
        while (totalWeight > maxWeight) {              // one big entry may push out several small ones
            val lru = tail.prev!!                      // never the new entry: it fits on its own
            unlink(lru); map.remove(lru.key!!)
            totalWeight -= lru.value.length
            evicted += lru.key
        }
    }

    // Keys, most recently used first.
    fun keys(): List<String> =
        generateSequence(head.next) { it.next }.takeWhile { it !== tail }.map { it.key!! }.toList()

    private fun unlink(n: Node) { n.prev!!.next = n.next; n.next!!.prev = n.prev }
    private fun addFront(n: Node) { n.next = head.next; n.prev = head; head.next!!.prev = n; head.next = n }
}
