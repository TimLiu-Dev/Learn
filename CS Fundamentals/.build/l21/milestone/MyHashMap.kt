// Minimal stand-in for your L19 MyHashMap (separate chaining). Swap in your own.
class MyHashMap<K, V> {
    private class Node<K, V>(val key: K, var value: V, var next: Node<K, V>?)

    private var buckets = arrayOfNulls<Node<K, V>>(16)
    var size = 0
        private set

    private fun indexFor(key: K, n: Int) = (key.hashCode() and 0x7fffffff) % n

    fun put(key: K, value: V) {
        val i = indexFor(key, buckets.size)
        var node = buckets[i]
        while (node != null) {
            if (node.key == key) { node.value = value; return }
            node = node.next
        }
        buckets[i] = Node(key, value, buckets[i])
        size++
        if (size > buckets.size * 3 / 4) resize()               // load factor 0.75
    }

    operator fun get(key: K): V? {
        var node = buckets[indexFor(key, buckets.size)]
        while (node != null) {
            if (node.key == key) return node.value
            node = node.next
        }
        return null
    }

    private fun resize() {
        val old = buckets
        buckets = arrayOfNulls(old.size * 2)
        for (head in old) {
            var node = head
            while (node != null) {
                val next = node.next
                val i = indexFor(node.key, buckets.size)
                node.next = buckets[i]
                buckets[i] = node
                node = next
            }
        }
    }
}
