class DoublyList<T> : Iterable<T> {
    class Node<T>(val value: T?) {                  // value is null only in the sentinels
        var prev: Node<T>? = null
        var next: Node<T>? = null
    }

    private val head = Node<T>(null)                // sentinel before the first element
    private val tail = Node<T>(null)                // sentinel after the last element
    var size = 0
        private set

    init { head.next = tail; tail.prev = head }     // empty list: the sentinels hold hands

    private fun linkBefore(succ: Node<T>, value: T): Node<T> {
        val pred = succ.prev!!
        val n = Node(value)
        n.prev = pred; n.next = succ                // 1. wire the new node in
        pred.next = n; succ.prev = n                // 2. then point the neighbours at it
        size++
        return n
    }

    fun addFirst(value: T) = linkBefore(head.next!!, value)    // O(1)
    fun addLast(value: T) = linkBefore(tail, value)             // O(1)

    fun remove(node: Node<T>): T {                  // O(1) when you already hold the node
        node.prev!!.next = node.next
        node.next!!.prev = node.prev
        size--
        @Suppress("UNCHECKED_CAST")
        return node.value as T
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cur = head.next!!
        override fun hasNext() = cur !== tail
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            @Suppress("UNCHECKED_CAST")
            val v = cur.value as T
            cur = cur.next!!
            return v
        }
    }

    fun backwards(): List<T> {
        val out = mutableListOf<T>()
        var cur = tail.prev!!
        @Suppress("UNCHECKED_CAST")
        while (cur !== head) { out.add(cur.value as T); cur = cur.prev!! }
        return out
    }
}

fun main() {
    val list = DoublyList<String>()
    list.addLast("B")
    val c = list.addLast("C")
    list.addFirst("A")
    list.addLast("D")
    for (s in list) print("$s ")                    // works because of iterator()
    println("| backwards ${list.backwards()}")
    println("removed ${list.remove(c)}, now ${list.toList()} size=${list.size}")
    println("joined: " + list.joinToString("-"))    // every Iterable gets the stdlib extras
}
