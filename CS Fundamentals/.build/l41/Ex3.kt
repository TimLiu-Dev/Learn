// Exercise 3: two features real caches have. peek() reads without changing recency,
// and onEvict tells you which entry was pushed out (to log it, or to close a resource).
class Lru<K : Any, V : Any>(private val capacity: Int, private val onEvict: (K, V) -> Unit) {
    private inner class Node(val key: K?, var value: V?) {
        var prev: Node? = null
        var next: Node? = null
    }
    private val map = HashMap<K, Node>()
    private val head = Node(null, null)
    private val tail = Node(null, null)

    init { head.next = tail; tail.prev = head }

    fun get(key: K): V? {
        val n = map[key] ?: return null
        unlink(n); addFront(n)
        return n.value
    }

    fun peek(key: K): V? = map[key]?.value                  // same lookup, no move to front

    fun put(key: K, value: V) {
        map[key]?.let { n -> n.value = value; unlink(n); addFront(n); return }
        if (map.size == capacity) {
            val oldest = tail.prev!!
            unlink(oldest); map.remove(oldest.key!!)
            onEvict(oldest.key, oldest.value!!)             // tell the owner
        }
        val n = Node(key, value)
        map[key] = n; addFront(n)
    }

    fun keys(): List<K> = generateSequence(head.next) { it.next }.takeWhile { it !== tail }.map { it.key!! }.toList()

    private fun unlink(n: Node) { n.prev!!.next = n.next; n.next!!.prev = n.prev }
    private fun addFront(n: Node) { n.next = head.next; n.prev = head; head.next!!.prev = n; head.next = n }
}

fun main() {
    val cache = Lru<String, String>(2) { k, v -> println("  evicted $k=$v") }
    cache.put("dune", "Herbert"); cache.put("emma", "Austen")
    println("peek(dune) = ${cache.peek("dune")}, order ${cache.keys()}")
    cache.put("ulysses", "Joyce")                           // dune is still the oldest
    println("order ${cache.keys()}")
    cache.get("emma")
    cache.put("beloved", "Morrison")                        // ulysses is now the oldest
    println("order ${cache.keys()}")
}
