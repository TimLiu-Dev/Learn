class Node(val key: Int, val left: Node? = null, val right: Node? = null)

fun main() {
    val root = Node(50, Node(30, Node(20), Node(40)), Node(70, Node(60), Node(80)))
    val out = mutableListOf<Int>()
    val todo = ArrayDeque<Node>()
    todo.addLast(root)
    while (todo.isNotEmpty()) {
        val n = todo.removeLast()           // was removeFirst()
        out += n.key
        n.left?.let { todo.addLast(it) }
        n.right?.let { todo.addLast(it) }
    }
    println(out)
}
