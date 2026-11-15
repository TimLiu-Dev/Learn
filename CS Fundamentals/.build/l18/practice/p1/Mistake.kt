class Node(val value: Int, val next: Node?)

// Base case 0 looks harmless, but 0 is not "no value".
fun maxValue(node: Node?): Int? {
    if (node == null) return 0
    return maxOf(node.value, maxValue(node.next)!!)
}

fun without(node: Node?, x: Int): Node? {
    if (node == null) return null
    val rest = without(node.next, x)
    return if (node.value == x) rest else Node(node.value, rest)
}
