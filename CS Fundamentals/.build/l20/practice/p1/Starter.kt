class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

fun height(node: Node?): Int =
    if (node == null) 0 else 1 + maxOf(height(node.left), height(node.right))

fun isBalanced(node: Node?): Boolean =
    TODO("compare the two subtree heights here, and check both subtrees too")

fun floor(root: Node?, key: Int): Int? =
    TODO("walk down; remember the best key <= key you've seen so far")
