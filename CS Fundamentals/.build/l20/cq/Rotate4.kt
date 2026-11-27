class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun rotateLeft(x: Node): Node {
    val y = x.right ?: return x
    x.right = y.left
    y.left = x
    return y
}

fun insert(n: Node?, key: Int): Node {
    if (n == null) return Node(key)
    if (key < n.key) n.left = insert(n.left, key) else if (key > n.key) n.right = insert(n.right, key)
    return n
}

fun height(n: Node?): Int = if (n == null) 0 else 1 + maxOf(height(n.left), height(n.right))
fun inOrder(n: Node?): List<Int> = if (n == null) emptyList() else inOrder(n.left) + n.key + inOrder(n.right)

fun main() {
    var root: Node? = null
    for (k in 1..4) root = insert(root, k)
    root = rotateLeft(root!!)
    println("${root.key} ${height(root)} ${inOrder(root)}")
}
