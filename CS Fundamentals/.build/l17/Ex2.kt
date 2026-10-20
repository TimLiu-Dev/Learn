// A recently-used list: the core of an LRU cache (Phase 12 builds the cache itself)
class RecentList<T> {
    class Node<T>(val value: T?) { var prev: Node<T>? = null; var next: Node<T>? = null }

    private val head = Node<T>(null)
    private val tail = Node<T>(null)
    var size = 0
        private set

    init { head.next = tail; tail.prev = head }

    private fun unlink(n: Node<T>) {
        n.prev!!.next = n.next
        n.next!!.prev = n.prev
    }

    private fun linkFirst(n: Node<T>) {
        n.prev = head; n.next = head.next
        head.next!!.prev = n; head.next = n
    }

    fun addFirst(value: T): Node<T> = Node(value).also { linkFirst(it); size++ }

    fun moveToFront(n: Node<T>) {                   // O(1): unlink, relink at the front
        unlink(n)
        linkFirst(n)
    }

    fun removeLast(): T {                           // O(1): the least recently used
        val last = tail.prev!!
        if (last === head) throw NoSuchElementException("list is empty")
        unlink(last)
        size--
        @Suppress("UNCHECKED_CAST")
        return last.value as T
    }

    override fun toString(): String {
        val parts = mutableListOf<String>()
        var cur = head.next!!
        while (cur !== tail) { parts.add(cur.value.toString()); cur = cur.next!! }
        return parts.toString()
    }
}

fun main() {
    val recent = RecentList<String>()
    val a = recent.addFirst("a.txt")
    recent.addFirst("b.txt")
    recent.addFirst("c.txt")
    println(recent)                                 // newest first
    recent.moveToFront(a)                           // a.txt was opened again
    println(recent)
    println("evict ${recent.removeLast()} -> $recent size=${recent.size}")
}
