class Node(val value: Int, val next: Node?)

// The largest value in the list, or null for an empty list.
fun maxValue(node: Node?): Int? = TODO("base case: empty list. Then compare node.value with the max of the rest")

// A new list without any x; the old nodes stay unchanged.
fun without(node: Node?, x: Int): Node? = TODO("base case: empty list. Then skip node or keep it in front of the rest")
