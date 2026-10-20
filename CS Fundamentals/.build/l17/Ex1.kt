class SinglyList<T> {
    private class Node<T>(val value: T, var next: Node<T>?)

    private var head: Node<T>? = null
    var size = 0
        private set

    fun addFirst(value: T) {                        // O(1)
        head = Node(value, head)
        size++
    }

    operator fun get(index: Int): T {               // O(n): walk index steps
        if (index !in 0 until size) throw IndexOutOfBoundsException("index $index, size $size")
        var cur = head!!
        repeat(index) { cur = cur.next!! }
        return cur.value
    }

    fun removeFirst(): T {                          // O(1)
        val h = head ?: throw NoSuchElementException("list is empty")
        head = h.next
        size--
        return h.value
    }

    fun contains(value: T): Boolean {               // O(n)
        var cur = head
        while (cur != null) {
            if (cur.value == value) return true
            cur = cur.next
        }
        return false
    }

    fun removeValue(value: T): Boolean {            // O(n): find the node BEFORE it
        val h = head ?: return false
        if (h.value == value) { head = h.next; size--; return true }
        var prev: Node<T> = h
        var cur = h.next
        while (cur != null) {
            if (cur.value == value) {
                prev.next = cur.next                // skip over cur
                size--
                return true
            }
            prev = cur
            cur = cur.next
        }
        return false
    }

    override fun toString(): String {
        val parts = mutableListOf<String>()
        var cur = head
        while (cur != null) { parts.add(cur.value.toString()); cur = cur.next }
        return parts.joinToString(", ", "[", "]")
    }
}

fun main() {
    val list = SinglyList<String>()
    list.addFirst("C"); list.addFirst("B"); list.addFirst("A")
    println("$list size=${list.size} list[1]=${list[1]}")
    println("contains B? ${list.contains("B")}  contains Z? ${list.contains("Z")}")
    println("remove B: ${list.removeValue("B")} -> $list")
    println("remove A: ${list.removeValue("A")} -> $list")
    println("remove Z: ${list.removeValue("Z")} -> $list")
    println("removeFirst: ${list.removeFirst()} -> $list size=${list.size}")
    try { list.removeFirst() } catch (e: NoSuchElementException) { println("empty: ${e.message}") }
    try { list[0] } catch (e: IndexOutOfBoundsException) { println("bad index: ${e.message}") }
}
