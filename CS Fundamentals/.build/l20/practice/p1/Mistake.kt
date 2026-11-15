class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

fun height(node: Node?): Int =
    if (node == null) 0 else 1 + maxOf(height(node.left), height(node.right))

// Checks only the root.
fun isBalanced(node: Node?): Boolean {
    if (node == null) return true
    return Math.abs(height(node.left) - height(node.right)) <= 1
}

fun floor(root: Node?, key: Int): Int? {
    var best: Int? = null
    var n = root
    while (n != null) {
        when {
            key < n.key -> n = n.left
            key > n.key -> { best = n.key; n = n.right }
            else -> return n.key
        }
    }
    return best
}
