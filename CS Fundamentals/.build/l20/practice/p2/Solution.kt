class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

// Lazy: each key is produced only when the caller asks for the next one.
fun inOrder(root: Node?): Sequence<Int> = sequence {
    if (root != null) {
        yieldAll(inOrder(root.left))
        yield(root.key)
        yieldAll(inOrder(root.right))
    }
}

// Sequences are 0-based: the k-th smallest (1-based) is element k - 1.
fun kthSmallest(root: Node?, k: Int): Int? =
    if (k < 1) null else inOrder(root).elementAtOrNull(k - 1)

// In-order with pruning: a left subtree only holds smaller keys, a right one only bigger.
fun between(root: Node?, lo: Int, hi: Int): List<Int> {
    val out = mutableListOf<Int>()
    fun walk(n: Node?) {
        if (n == null) return
        if (lo < n.key) walk(n.left)          // anything in range on the left?
        if (n.key in lo..hi) out += n.key
        if (n.key < hi) walk(n.right)         // anything in range on the right?
    }
    walk(root)
    return out
}
