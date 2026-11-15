class DoublyList<T> : Iterable<T> {
    class Node<T>(val value: T?) {                  // value is null only in the sentinels
        var prev: Node<T>? = null
        var next: Node<T>? = null
    }

    private var head = Node<T>(null)                // sentinel before the first element
    private var tail = Node<T>(null)                // sentinel after the last element
    var size = 0
        private set

    init { head.next = tail; tail.prev = head }

    private fun linkBefore(succ: Node<T>, value: T): Node<T> {
        val pred = succ.prev!!
        val n = Node(value)
        n.prev = pred; n.next = succ
        pred.next = n; succ.prev = n
        size++
        return n
    }

    fun addFirst(value: T) = linkBefore(head.next!!, value)
    fun addLast(value: T) = linkBefore(tail, value)

    fun remove(node: Node<T>): T {
        node.prev!!.next = node.next
        node.next!!.prev = node.prev
        size--
        @Suppress("UNCHECKED_CAST")
        return node.value as T
    }

    fun reverse() {                                 // O(n), no new nodes
        var cur: Node<T>? = head
        while (cur != null) {                       // visits the sentinels too
            val next = cur.next                     // save it before the swap (Part 1's rule)
            cur.next = cur.prev
            cur.prev = next
            cur = next
        }
        val oldHead = head                          // the sentinels swap roles as well
        head = tail
        tail = oldHead
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
