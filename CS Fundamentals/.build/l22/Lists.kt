import java.util.LinkedList

const val N = 5_000_000

fun bestMs(f: () -> Long): Pair<Double, Long> {
    var best = Double.MAX_VALUE
    var result = 0L
    repeat(5) {
        val t = System.nanoTime()
        result = f()
        best = minOf(best, (System.nanoTime() - t) / 1e6)
    }
    return best to result
}

fun main() {
    val array = IntArray(N) { it }                       // N ints side by side
    val boxed = ArrayList<Int>(N).apply { for (i in 0 until N) add(i) }  // N pointers to Integer objects
    val shuffled = ArrayList(boxed).apply { shuffle(java.util.Random(1)) } // same objects, visited out of memory order
    val linked = LinkedList(boxed)                       // N nodes, each pointing to the next

    val rows = listOf(
        "IntArray" to bestMs { var s = 0L; for (x in array) s += x; s },
        "ArrayList<Int>" to bestMs { var s = 0L; for (x in boxed) s += x; s },
        "ArrayList, shuffled" to bestMs { var s = 0L; for (x in shuffled) s += x; s },
        "LinkedList<Int>" to bestMs { var s = 0L; for (x in linked) s += x; s },
    )
    val base = rows[0].second.first
    for ((name, r) in rows) println("%-20s %7.1f ms  %5.1fx  (sum ${r.second})".format(name, r.first, r.first / base))
}
