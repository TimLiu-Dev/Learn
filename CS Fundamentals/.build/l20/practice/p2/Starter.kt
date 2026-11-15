class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

fun inOrder(root: Node?): Sequence<Int> =
    TODO("sequence { … }: left subtree, then the node, then the right subtree")

fun kthSmallest(root: Node?, k: Int): Int? =
    TODO("the in-order sequence already counts for you")

fun between(root: Node?, lo: Int, hi: Int): List<Int> =
    TODO("an in-order walk that only goes left or right when it could find a match")
