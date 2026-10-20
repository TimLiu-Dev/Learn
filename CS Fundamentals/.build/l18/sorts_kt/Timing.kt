import java.util.Random

fun randomInts(n: Int, seed: Long): IntArray {
    val rnd = Random(seed)
    return IntArray(n) { rnd.nextInt() }
}

// Best of 3 runs, in milliseconds. Each run sorts a fresh copy.
fun timeMs(input: IntArray, sort: (IntArray) -> Unit): Double =
    (1..3).minOf {
        val a = input.copyOf()
        val start = System.nanoTime()
        sort(a)
        (System.nanoTime() - start) / 1e6
    }

fun main() {
    val sorts = listOf<Pair<String, (IntArray) -> Unit>>(
        "insertion" to ::insertionSort, "merge" to ::mergeSort,
        "quick" to { a -> quickSort(a) }, "Arrays.sort" to { a -> a.sort() })
    repeat(5) { warm -> sorts.forEach { (_, s) -> timeMs(randomInts(5_000, warm.toLong()), s) } }  // JIT warm-up

    println("       n" + sorts.joinToString("") { "%14s".format(it.first) })
    val prev = DoubleArray(sorts.size)
    for (n in listOf(10_000, 20_000, 40_000, 80_000)) {
        val input = randomInts(n, 42)
        val row = StringBuilder("%,8d".format(n))
        sorts.forEachIndexed { k, (_, s) ->
            val ms = timeMs(input, s)
            val ratio = if (prev[k] > 0) "x%.1f".format(ms / prev[k]) else ""
            row.append("%9.2f%5s".format(ms, ratio))
            prev[k] = ms
        }
        println(row)
    }
}
