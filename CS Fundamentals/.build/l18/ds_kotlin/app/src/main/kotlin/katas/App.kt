package katas

import java.util.Random

private fun randomInts(n: Int): List<Int> {
    val rnd = Random(42)
    return List(n) { rnd.nextInt() }
}

// Best of 3 runs in ms, each on a fresh copy of the input.
private fun timeMs(input: List<Int>, sort: (MutableList<Int>) -> Unit): Double =
    (1..3).minOf {
        val xs = input.toMutableList()
        val start = System.nanoTime()
        sort(xs)
        (System.nanoTime() - start) / 1e6
    }

fun main() {
    val sorts = listOf<Pair<String, (MutableList<Int>) -> Unit>>(
        "insertion" to { insertionSort(it) },
        "merge" to { mergeSort(it) },
        "quick" to { quickSort(it) },
        "JDK sort()" to { it.sort() },                       // TimSort on boxed Integers
        "IntArray" to { it.toIntArray().sort() })            // Dual-Pivot Quicksort on ints (+ copy)
    repeat(5) { sorts.forEach { (_, s) -> timeMs(randomInts(5_000), s) } }   // JIT warm-up

    println("       n" + sorts.joinToString("") { "%12s".format(it.first) })
    for (n in listOf(10_000, 20_000, 40_000, 80_000)) {
        val input = randomInts(n)
        println("%,8d".format(n) + sorts.joinToString("") { (_, s) -> "%12.2f".format(timeMs(input, s)) })
    }
    println("(milliseconds, best of 3, on my machine; yours will differ)")
}
