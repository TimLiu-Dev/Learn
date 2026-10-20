class Node(val key: Int, var left: Node? = null, var right: Node? = null)

fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)
    if (key < node.key) node.left = insert(node.left, key)
    else if (key > node.key) node.right = insert(node.right, key)
    return node
}

// A lazy in-order walk: the recursion pauses at every yield (L15).
fun inOrder(n: Node?): Sequence<Int> = sequence {
    if (n != null) {
        yieldAll(inOrder(n.left))
        println("  visit ${n.key}")
        yield(n.key)
        yieldAll(inOrder(n.right))
    }
}

// The same walk with an explicit stack instead of the call stack (L17/L18).
fun inOrderStack(root: Node?): List<Int> {
    val out = mutableListOf<Int>()
    val stack = ArrayDeque<Node>()
    var cur = root
    while (cur != null || stack.isNotEmpty()) {
        while (cur != null) { stack.addLast(cur); cur = cur.left }  // dive left, saving the path
        val n = stack.removeLast()                                  // the leftmost unvisited node
        out += n.key
        cur = n.right                                               // then do its right subtree
    }
    return out
}

fun main() {
    var root: Node? = null
    for (k in listOf(50, 30, 70, 20, 40, 60, 80)) root = insert(root, k)
    println("first key > 35: " + inOrder(root).first { it > 35 })
    println("stack version:  " + inOrderStack(root))
}
