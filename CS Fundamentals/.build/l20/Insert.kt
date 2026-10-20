class Node(val key: Int, var left: Node? = null, var right: Node? = null)

// Recursive insert returns the (possibly new) root of this subtree.
// The caller re-links it: that's how a null link becomes a new node.
fun insert(node: Node?, key: Int): Node {
    if (node == null) return Node(key)               // base case: empty spot found
    val c = key.compareTo(node.key)
    when {
        c < 0 -> node.left = insert(node.left, key)
        c > 0 -> node.right = insert(node.right, key)
        else -> {}                                    // duplicate: ignore it
    }
    return node
}

// Iterative insert: walk down remembering the parent, then hang the node off it.
fun insertLoop(root: Node?, key: Int): Node {
    if (root == null) return Node(key)
    var cur: Node = root
    while (true) {
        val c = key.compareTo(cur.key)
        cur = when {
            c < 0 -> cur.left ?: run { cur.left = Node(key); return root }
            c > 0 -> cur.right ?: run { cur.right = Node(key); return root }
            else -> return root
        }
    }
}

// Smallest key: keep going left. Largest: keep going right.
fun min(root: Node): Int { var n = root; while (true) n = n.left ?: return n.key }
fun max(root: Node): Int { var n = root; while (true) n = n.right ?: return n.key }

// Height = nodes on the longest root-to-leaf path. Empty tree = 0.
fun height(node: Node?): Int =
    if (node == null) 0 else 1 + maxOf(height(node.left), height(node.right))

fun main() {
    var root: Node? = null
    for (k in listOf(50, 30, 70, 20, 40, 60, 80)) root = insert(root, k)
    root = insertLoop(root, 65)
    root = insert(root, 40)                           // duplicate, ignored
    println("min=${min(root)} max=${max(root)} height=${height(root)}")
    println("70's left is ${root.right?.left?.key}, whose right is ${root.right?.left?.right?.key}")
}
