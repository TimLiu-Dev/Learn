class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(n: Node?, key: Int): Node {
    if (n == null) return Node(key)
    if (key < n.key) n.left = insert(n.left, key) else n.right = insert(n.right, key)
    return n
}

fun preOrder(n: Node?, out: MutableList<Int>) {
    if (n == null) return
    out += n.key
    preOrder(n.left, out)
    preOrder(n.right, out)
}

fun main() {
    var root: Node? = null
    for (k in listOf(40, 60, 20, 50, 10, 30)) root = insert(root, k)
    println(mutableListOf<Int>().also { preOrder(root, it) })
}
