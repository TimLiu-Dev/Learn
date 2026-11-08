import kotlin.random.Random

// Maximum of every window of size k, in O(n) total, with a monotonic deque of INDEXES.
// Invariant: values at the indexes in the deque are strictly decreasing from front to back,
// so the front is always the current window's maximum.
fun windowMax(a: IntArray, k: Int, trace: Boolean = false): IntArray {
    require(k in 1..a.size)
    val out = IntArray(a.size - k + 1)
    val dq = ArrayDeque<Int>()                           // kotlin.collections.ArrayDeque, no import (L17)
    for (i in a.indices) {
        if (dq.isNotEmpty() && dq.first() <= i - k) dq.removeFirst()          // front slid out of the window
        while (dq.isNotEmpty() && a[dq.last()] <= a[i]) dq.removeLast()        // smaller ones can never be a max again
        dq.addLast(i)
        if (i >= k - 1) out[i - k + 1] = a[dq.first()]
        if (trace) println("  i=$i a=${a[i]}  deque values=${dq.map { a[it] }}" +
            if (i >= k - 1) "  max=${a[dq.first()]}" else "")
    }
    return out
}

fun windowMaxBrute(a: IntArray, k: Int) = IntArray(a.size - k + 1) { s -> (s until s + k).maxOf { a[it] } }

fun main() {
    val a = intArrayOf(1, 3, -1, -3, 5, 3, 6, 7)
    println("k=3:")
    println("-> ${windowMax(a, 3, trace = true).contentToString()}")

    // Differential test: compare against the obvious O(n*k) version on 10,000 random inputs.
    val rnd = Random(50)
    repeat(10_000) {
        val n = rnd.nextInt(1, 30)
        val x = IntArray(n) { rnd.nextInt(-5, 6) }       // small range: lots of ties
        val k = rnd.nextInt(1, n + 1)
        check(windowMax(x, k).contentEquals(windowMaxBrute(x, k))) { "mismatch on ${x.contentToString()} k=$k" }
    }
    println("10000 random cases agree with brute force")
}
