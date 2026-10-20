package katas

class MyLinkedList<T> : Iterable<T> {
    private class Node<T>(val value: T, var next: Node<T>?)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    var size = 0
        private set

    fun isEmpty() = size == 0

    fun addFirst(value: T) {                        // O(1)
        val n = Node(value, head)
        head = n
        if (tail == null) tail = n                  // the only node is also the last
        size++
    }

    fun addLast(value: T) {                         // O(1) thanks to the tail pointer
        val n = Node(value, null)
        val t = tail
        if (t == null) head = n else t.next = n
        tail = n
        size++
    }

    fun removeFirst(): T {                          // O(1)
        val h = head ?: throw NoSuchElementException("list is empty")
        head = h.next
        if (head == null) tail = null               // emptied: clear the tail too
        size--
        return h.value
    }

    fun peekFirst(): T? = head?.value               // O(1)

    operator fun get(index: Int): T {               // O(n)
        if (index !in 0 until size) throw IndexOutOfBoundsException("index $index, size $size")
        var cur = head!!
        repeat(index) { cur = cur.next!! }
        return cur.value
    }

    fun reverse() {                                 // O(n), no new nodes
        var prev: Node<T>? = null
        var curr = head
        tail = head                                 // the old head becomes the tail
        while (curr != null) {
            val next = curr.next
            curr.next = prev
            prev = curr
            curr = next
        }
        head = prev
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cur = head
        override fun hasNext() = cur != null
        override fun next(): T {
            val n = cur ?: throw NoSuchElementException()
            cur = n.next
            return n.value
        }
    }

    override fun toString() = joinToString(", ", "[", "]")
}
