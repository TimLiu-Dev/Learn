class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

fun height(node: Node?): Int =
    if (node == null) 0 else 1 + maxOf(height(node.left), height(node.right))

// The rule must hold at EVERY node, not only at the root.
fun isBalanced(node: Node?): Boolean {
    if (node == null) return true
    val diff = height(node.left) - height(node.right)
    return diff in -1..1 && isBalanced(node.left) && isBalanced(node.right)
}

// One path down. Going right means this key is <= key: remember it as the best so far.
fun floor(root: Node?, key: Int): Int? {
    var best: Int? = null
    var n = root
    while (n != null) {
        when {
            key < n.key -> n = n.left          // too big: the floor must be on the left
            key > n.key -> { best = n.key; n = n.right }
            else -> return n.key               // exact match is its own floor
        }
    }
    return best
}
