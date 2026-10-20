import java.util.Collections
import java.util.Random

class Node(val key: Int, var left: Node? = null, var right: Node? = null)

// Iterative insert that also counts comparisons, so deep trees can't overflow the stack.
var comparisons = 0L
fun insert(root: Node?, key: Int): Node {
    if (root == null) return Node(key)
    var cur: Node = root
    while (true) {
        comparisons++
        cur = if (key < cur.key) cur.left ?: run { cur.left = Node(key); return root }
              else cur.right ?: run { cur.right = Node(key); return root }
    }
}

fun height(n: Node?): Int = if (n == null) 0 else 1 + maxOf(height(n.left), height(n.right))

fun build(keys: List<Int>): Node? {
    var root: Node? = null
    for (k in keys) root = insert(root, k)
    return root
}

fun main() {
    val sorted = (1..1000).toList()
    // java.util.Random + Collections.shuffle, so Kotlin and Java get the same order.
    val shuffled = sorted.toMutableList().also { Collections.shuffle(it, Random(42)) }
    println("n=1000  sorted height=${height(build(sorted))}  shuffled height=${height(build(shuffled))}")

    for (n in listOf(10_000, 20_000, 40_000)) {
        val keys = (1..n).toList()
        val mixed = keys.toMutableList().also { Collections.shuffle(it, Random(42)) }
        comparisons = 0; var t = System.nanoTime(); build(keys)
        val sortedMs = (System.nanoTime() - t) / 1_000_000; val sortedCmp = comparisons
        comparisons = 0; t = System.nanoTime(); build(mixed)
        val mixedMs = (System.nanoTime() - t) / 1_000_000; val mixedCmp = comparisons
        println("n=$n  sorted: $sortedCmp comparisons, $sortedMs ms   shuffled: $mixedCmp comparisons, $mixedMs ms")
    }
}
