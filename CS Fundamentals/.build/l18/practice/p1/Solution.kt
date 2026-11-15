class Node(val value: Int, val next: Node?)

// The largest value in the list, or null for an empty list.
fun maxValue(node: Node?): Int? {
    if (node == null) return null                // empty list: there is no max
    val rest = maxValue(node.next) ?: return node.value
    return maxOf(node.value, rest)
}

// A new list without any x; the old nodes stay unchanged.
fun without(node: Node?, x: Int): Node? {
    if (node == null) return null                // nothing left to filter
    val rest = without(node.next, x)             // trust the recursion for the tail
    return if (node.value == x) rest else Node(node.value, rest)
}
