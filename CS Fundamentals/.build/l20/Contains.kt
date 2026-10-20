// One node of a binary search tree: a key and two subtrees (null = empty).
class Node(val key: Int, var left: Node? = null, var right: Node? = null)

// Recursive. Base cases: an empty tree (not found) and a match.
fun containsRec(node: Node?, key: Int): Boolean {
    if (node == null) return false
    val c = key.compareTo(node.key)
    return when {
        c < 0 -> containsRec(node.left, key)   // smaller: only the left can hold it
        c > 0 -> containsRec(node.right, key)  // bigger: only the right can hold it
        else -> true
    }
}

// Iterative: walk ONE path down, printing each key we compare against.
fun containsLoop(root: Node?, key: Int): Boolean {
    var cur = root
    while (cur != null) {
        print("${cur.key} ")
        val c = key.compareTo(cur.key)
        cur = when {
            c < 0 -> cur.left
            c > 0 -> cur.right
            else -> return true
        }
    }
    return false
}

fun main() {
    //         50
    //       /    \
    //     30      70
    //    /  \    /  \
    //   20  40  60  80
    val root = Node(50,
        Node(30, Node(20), Node(40)),
        Node(70, Node(60), Node(80)))
    println("rec 60: ${containsRec(root, 60)}, rec 45: ${containsRec(root, 45)}")
    print("loop 60: "); println(containsLoop(root, 60))
    print("loop 45: "); println(containsLoop(root, 45))
}
