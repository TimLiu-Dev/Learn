import java.util.TreeMap
import kotlin.random.Random

// Runs block 3 times and returns the fastest, in ms. The slower runs are mostly noise (GC, JIT).
fun ms(block: () -> Unit): Double = (1..3).minOf {
    val t0 = System.nanoTime()
    block()
    (System.nanoTime() - t0) / 1e6
}

var sink = 0L   // results go here so the JIT can't delete the work as unused

fun measure(n: Int): Map<String, Double> {
    val keys = (0 until n).shuffled(Random(n))   // random order keeps the BST bushy
    return linkedMapOf(
        "MyArrayList" to ms { val l = MyArrayList<Int>(); for (k in keys) l.add(k); for (i in 0 until n) sink += l[i] },
        "ArrayList" to ms { val l = ArrayList<Int>(); for (k in keys) l.add(k); for (i in 0 until n) sink += l[i] },
        "MyHashMap" to ms { val m = MyHashMap<Int, Int>(); for (k in keys) m.put(k, k); for (k in keys) sink += m[k]!! },
        "HashMap" to ms { val m = HashMap<Int, Int>(); for (k in keys) m[k] = k; for (k in keys) sink += m[k]!! },
        "MyBST" to ms { val t = MyBST<Int, Int>(); for (k in keys) t.put(k, k); for (k in keys) sink += t[k]!! },
        "TreeMap" to ms { val t = TreeMap<Int, Int>(); for (k in keys) t[k] = k; for (k in keys) sink += t[k]!! },
    )
}

fun main() {
    repeat(3) { measure(200_000) }                    // warm-up: let the JIT compile everything
    val sizes = listOf(250_000, 500_000, 1_000_000, 2_000_000)
    println("n added then read back; time in ms (ratio vs previous n)")
    println("%-12s".format("") + sizes.joinToString("") { "%16d".format(it) })
    val results = sizes.map { measure(it) }
    for (name in results[0].keys) {
        val cells = results.mapIndexed { i, r ->
            val t = r.getValue(name)
            if (i == 0) "%16.1f".format(t) else "%9.1f (%.1f)".format(t, t / results[i - 1].getValue(name))
        }
        println("%-12s".format(name) + cells.joinToString(""))
    }

    val sorted = (0 until 20_000).toList()             // the worst case for an unbalanced BST
    val bst = ms { val t = MyBST<Int, Int>(); for (k in sorted) t.put(k, k); for (k in sorted) sink += t[k]!! }
    val tree = ms { val t = TreeMap<Int, Int>(); for (k in sorted) t[k] = k; for (k in sorted) sink += t[k]!! }
    println("sorted keys, n=20000: MyBST %.1f ms, TreeMap %.1f ms".format(bst, tree))
}
