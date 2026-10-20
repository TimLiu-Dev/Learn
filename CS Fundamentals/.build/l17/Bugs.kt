class Node(val value: String, var next: Node?)

// BUG 1: overwrite node.next first, and the only link to the rest is gone
fun insertAfterBuggy(node: Node, value: String) {
    val fresh = Node(value, null)
    node.next = fresh
    fresh.next = node.next          // node.next IS fresh now: it points at itself
}

fun insertAfter(node: Node, value: String) {
    node.next = Node(value, node.next)  // read the old link before replacing it
}

// BUG 2: a list with a tail pointer that forgets to clear it
class TailList {
    var head: Node? = null
    var tail: Node? = null
    var size = 0
    fun addLast(value: String) {
        val n = Node(value, null)
        val t = tail
        if (t == null) { head = n; tail = n } else { t.next = n; tail = n }
        size++
    }
    fun removeFirst(): String {
        val h = head ?: throw NoSuchElementException()
        head = h.next
        size--
        // missing: if (head == null) tail = null
        return h.value
    }
}

fun show(head: Node?, limit: Int = 6): String {
    val parts = mutableListOf<String>()
    var cur = head
    while (cur != null && parts.size < limit) { parts.add(cur.value); cur = cur.next }
    return parts.joinToString(" -> ") + if (cur != null) " -> ..." else ""
}

fun main() {
    val a = Node("A", Node("B", Node("C", null)))
    insertAfter(a, "X")
    println("fixed: " + show(a))
    val b = Node("A", Node("B", Node("C", null)))
    insertAfterBuggy(b, "X")
    println("buggy: " + show(b))

    val list = TailList()
    list.addLast("A")
    list.removeFirst()
    list.addLast("B")
    println("size=${list.size} contents=[${show(list.head)}]")
}
