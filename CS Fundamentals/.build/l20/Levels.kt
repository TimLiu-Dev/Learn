class Node(val key: Int, var left: Node? = null, var right: Node? = null)

// One line per level: process the queue in batches of "everything on this level".
fun printLevels(root: Node?) {
    val queue = ArrayDeque<Node>()
    if (root != null) queue.addLast(root)
    var depth = 0
    while (queue.isNotEmpty()) {
        val line = mutableListOf<Int>()
        repeat(queue.size) {                        // size is fixed BEFORE we add children
            val n = queue.removeFirst()
            line += n.key
            n.left?.let { queue.addLast(it) }
            n.right?.let { queue.addLast(it) }
        }
        println("depth $depth: $line")
        depth++
    }
}

// The trap: comparing each node only with its own children.
fun isBstNaive(n: Node?): Boolean {
    if (n == null) return true
    if (n.left != null && n.left!!.key >= n.key) return false
    if (n.right != null && n.right!!.key <= n.key) return false
    return isBstNaive(n.left) && isBstNaive(n.right)
}

// Correct: every key must fit the (lo, hi) window inherited from ALL its ancestors.
fun isBst(n: Node?, lo: Int? = null, hi: Int? = null): Boolean {
    if (n == null) return true
    if (lo != null && n.key <= lo) return false
    if (hi != null && n.key >= hi) return false
    return isBst(n.left, lo, n.key) && isBst(n.right, n.key, hi)
}

fun main() {
    val good = Node(50, Node(30, Node(20), Node(40)), Node(70, Node(60), Node(80)))
    printLevels(good)
    //        50
    //      /    \
    //    30      70
    //   /  \
    //  20   60    <- 60 > 30 is fine locally, but it sits LEFT of 50
    val sneaky = Node(50, Node(30, Node(20), Node(60)), Node(70))
    println("good:   naive=${isBstNaive(good)} correct=${isBst(good)}")
    println("sneaky: naive=${isBstNaive(sneaky)} correct=${isBst(sneaky)}")
}
