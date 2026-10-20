class Node(val value: Int, var next: Node?)

fun reverse(head: Node?): Node? {
    var prev: Node? = null
    var curr = head
    while (curr != null) {
        val next = curr.next        // 1. remember the rest of the list
        curr.next = prev            // 2. flip this node's arrow backwards
        prev = curr                 // 3. step prev forward
        curr = next                 // 4. step curr forward
    }
    return prev                     // the old tail is the new head
}

fun main() {
    val list = Node(1, Node(2, Node(3, null)))
    val reversed = reverse(list)
    var n = reversed
    while (n != null) { print("${n.value} "); n = n.next }
    println()
    println(list.next == null)      // the old head is now the tail
}
