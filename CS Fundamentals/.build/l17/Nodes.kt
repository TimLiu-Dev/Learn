class Node<T>(val value: T, var next: Node<T>?)

fun main() {
    var head: Node<String>? = null
    head = Node("C", head)          // addFirst: the new node points at the old head
    head = Node("B", head)
    head = Node("A", head)
    printAll(head)
    head.next = head.next?.next     // unlink B: A now points straight at C
    printAll(head)
}

fun printAll(head: Node<String>?) {
    val sb = StringBuilder()
    var cur = head
    while (cur != null) {
        sb.append(cur.value).append(" -> ")
        cur = cur.next
    }
    println(sb.append("null"))
}
