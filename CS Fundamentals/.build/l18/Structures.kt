// A linked list is "empty, or a node followed by a smaller list".
class Node(val value: Int, val next: Node?)

fun length(node: Node?): Int =
    if (node == null) 0 else 1 + length(node.next)

fun sum(node: Node?): Int =
    if (node == null) 0 else node.value + sum(node.next)

// A tree is "empty, or a node with two smaller trees". Preview of L20.
class Tree(val value: Int, val left: Tree? = null, val right: Tree? = null)

fun size(t: Tree?): Int =
    if (t == null) 0 else 1 + size(t.left) + size(t.right)

fun height(t: Tree?): Int =
    if (t == null) 0 else 1 + maxOf(height(t.left), height(t.right))

fun main() {
    val list = Node(3, Node(1, Node(4, null)))
    println("length=${length(list)} sum=${sum(list)}")

    //        8
    //      /   \
    //     3     10
    //    / \      \
    //   1   6      14
    val tree = Tree(8, Tree(3, Tree(1), Tree(6)), Tree(10, right = Tree(14)))
    println("size=${size(tree)} height=${height(tree)}")
}
