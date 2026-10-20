class Node(val key: Int, var left: Node? = null)

fun leftIsSmaller(n: Node): Boolean =
    n.left != null && n.left.key < n.key
