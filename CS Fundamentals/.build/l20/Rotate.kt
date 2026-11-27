class Node(val key: Int, var left: Node? = null, var right: Node? = null)

// Left rotation around x: its right child y moves up, and x becomes y's left child.
// y's old left subtree (keys between x and y) moves across to become x's right.
// Returns the new root of this subtree, so the caller relinks it, as insert does.
fun rotateLeft(x: Node): Node {
    val y = x.right ?: return x          // no right child: nothing to rotate
    x.right = y.left
    y.left = x
    return y
}

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

fun height(n: Node?): Int = if (n == null) 0 else 1 + maxOf(height(n.left), height(n.right))
fun inOrder(n: Node?): List<Int> = if (n == null) emptyList() else inOrder(n.left) + n.key + inOrder(n.right)
fun shape(n: Node?): String = when {
    n == null -> "."
    n.left == null && n.right == null -> "${n.key}"
    else -> "(${shape(n.left)} ${n.key} ${shape(n.right)})"
}
fun report(label: String, root: Node?) =
    println("${label.padEnd(8)} ${shape(root).padEnd(24)} height=${height(root)} in-order=${inOrder(root)}")

fun main() {
    var stick: Node? = null
    for (k in 1..3) stick = insert(stick, k)        // sorted input: a stick
    report("stick", stick)
    stick = rotateLeft(stick!!)
    report("rotated", stick)

    var t: Node? = null
    for (k in listOf(10, 5, 20, 15, 30)) t = insert(t, k)
    report("before", t)
    t = rotateLeft(t!!)                             // 15 moves from 20's left to 10's right
    report("after", t)
}
