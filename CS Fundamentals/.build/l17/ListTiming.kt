import java.util.LinkedList

fun sumAll(list: List<Int>): Long {
    var total = 0L
    for (x in list) total += x                      // iterator: O(n) for BOTH lists
    return total
}

fun sumByIndex(list: List<Int>): Long {
    var total = 0L
    for (i in list.indices) total += list[i]        // get(i): O(1) array, O(n) linked
    return total
}

fun bestMillis(runs: Int, block: () -> Long): Double {
    var best = Long.MAX_VALUE
    repeat(runs) {
        val start = System.nanoTime()
        val result = block()
        val elapsed = System.nanoTime() - start
        if (result == 42L) println()                // use the result so it can't be skipped
        best = minOf(best, elapsed)
    }
    return best / 1e6
}

fun main() {
    val n = 1_000_000
    val array = ArrayList<Int>((0 until n).toList())
    val linked = LinkedList<Int>(array)
    repeat(3) { sumAll(array); sumAll(linked) }     // warm-up: let the JIT compile
    println("iterate 1M:  ArrayList %.1f ms   LinkedList %.1f ms".format(
        bestMillis(7) { sumAll(array) }, bestMillis(7) { sumAll(linked) }))

    var previous = 0.0
    for (size in listOf(5_000, 10_000, 20_000, 40_000)) {
        val a = ArrayList<Int>((0 until size).toList())
        val l = LinkedList<Int>(a)
        val ta = bestMillis(3) { sumByIndex(a) }
        val tl = bestMillis(3) { sumByIndex(l) }
        val ratio = if (previous > 0) "  x%.1f".format(tl / previous) else ""
        println("get(i) loop n=%6d: ArrayList %6.2f ms   LinkedList %8.1f ms%s".format(size, ta, tl, ratio))
        previous = tl
    }
}
