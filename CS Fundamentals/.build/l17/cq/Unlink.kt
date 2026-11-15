class Node(val value: String) {
    var prev: Node? = null
    var next: Node? = null
}

fun forwards(first: Node): String {
    val sb = StringBuilder()
    var cur: Node? = first
    while (cur != null) { sb.append(cur.value); cur = cur.next }
    return sb.toString()
}

fun backwards(last: Node): String {
    val sb = StringBuilder()
    var cur: Node? = last
    while (cur != null) { sb.append(cur.value); cur = cur.prev }
    return sb.toString()
}

fun main() {
    val a = Node("A"); val b = Node("B"); val c = Node("C")
    a.next = b; b.prev = a
    b.next = c; c.prev = b

    // unlink b, tidying up its own links as we go
    b.prev?.next = b.next
    b.prev = null
    b.next?.prev = b.prev
    b.next = null

    println(forwards(a) + " " + backwards(c))
}
