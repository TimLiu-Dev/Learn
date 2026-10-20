class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

// The three depth-first orders differ only in WHERE the node itself is visited.
fun preOrder(n: Node?, out: MutableList<Int>) {
    if (n == null) return
    out += n.key                 // node first
    preOrder(n.left, out); preOrder(n.right, out)
}
fun inOrder(n: Node?, out: MutableList<Int>) {
    if (n == null) return
    inOrder(n.left, out)
    out += n.key                 // node in the middle
    inOrder(n.right, out)
}
fun postOrder(n: Node?, out: MutableList<Int>) {
    if (n == null) return
    postOrder(n.left, out); postOrder(n.right, out)
    out += n.key                 // node last
}

// Breadth-first: a FIFO queue (L17) visits the tree level by level.
fun levelOrder(root: Node?): List<Int> {
    val out = mutableListOf<Int>()
    val queue = ArrayDeque<Node>()
    if (root != null) queue.addLast(root)
    while (queue.isNotEmpty()) {
        val n = queue.removeFirst()
        out += n.key
        n.left?.let { queue.addLast(it) }
        n.right?.let { queue.addLast(it) }
    }
    return out
}

fun main() {
    var root: Node? = null
    for (k in listOf(50, 30, 70, 20, 40, 60, 80)) root = insert(root, k)
    println("pre   " + mutableListOf<Int>().also { preOrder(root, it) })
    println("in    " + mutableListOf<Int>().also { inOrder(root, it) })
    println("post  " + mutableListOf<Int>().also { postOrder(root, it) })
    println("level " + levelOrder(root))
}
