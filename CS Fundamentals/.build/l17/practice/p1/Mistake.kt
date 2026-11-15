class Node<T>(val value: T, var next: Node<T>?)

fun <T> hasCycle(head: Node<T>?): Boolean {
    val seen = HashSet<T>()                 // remembers values, not nodes
    var cur = head
    while (cur != null) {
        if (!seen.add(cur.value)) return true
        cur = cur.next
    }
    return false
}
