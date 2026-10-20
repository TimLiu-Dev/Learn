class Node(var key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

// Like insert, delete returns the new root of this subtree and the caller re-links it.
fun delete(node: Node?, key: Int): Node? {
    if (node == null) return null                       // key not in the tree
    val c = key.compareTo(node.key)
    if (c < 0) node.left = delete(node.left, key)
    else if (c > 0) node.right = delete(node.right, key)
    else {
        // Cases 1 and 2: at most one child. Hand the other side up (null for a leaf).
        val left = node.left ?: return node.right
        val right = node.right ?: return left
        // Case 3: two children. Copy in the in-order successor (the smallest key
        // on the right), then delete that successor from the right subtree.
        var succ = right
        while (true) succ = succ.left ?: break
        node.key = succ.key
        node.right = delete(right, succ.key)
    }
    return node
}

// Compact picture of the tree: key(left right), "-" for an empty subtree.
fun shape(n: Node?): String = when {
    n == null -> "-"
    n.left == null && n.right == null -> "${n.key}"
    else -> "${n.key}(${shape(n.left)} ${shape(n.right)})"
}

fun main() {
    var root: Node? = null
    for (k in listOf(50, 30, 70, 20, 40, 60, 80, 65)) root = insert(root, k)
    println("start      ${shape(root)}")
    root = delete(root, 20); println("delete 20  ${shape(root)}")   // leaf
    root = delete(root, 60); println("delete 60  ${shape(root)}")   // one child
    root = delete(root, 50); println("delete 50  ${shape(root)}")   // two children
    root = delete(root, 99); println("delete 99  ${shape(root)}")   // not there
}
