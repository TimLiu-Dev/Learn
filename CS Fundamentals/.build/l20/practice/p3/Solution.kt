fun swap(a: IntArray, i: Int, j: Int) { val t = a[i]; a[i] = a[j]; a[j] = t }

// Checking each child against its parent covers every parent-child pair once.
fun isMinHeap(a: IntArray): Boolean = (1 until a.size).all { a[it] >= a[(it - 1) / 2] }

fun siftDown(a: IntArray, i: Int, n: Int) {
    var p = i
    while (true) {
        val l = 2 * p + 1
        if (l >= n) return                                   // no children: done
        val c = if (l + 1 < n && a[l + 1] < a[l]) l + 1 else l   // the SMALLER child
        if (a[p] <= a[c]) return                             // already no bigger than both
        swap(a, p, c)
        p = c
    }
}

// Bottom-up: when we reach a parent, both of its subtrees are already heaps.
fun heapify(a: IntArray) {
    for (i in a.size / 2 - 1 downTo 0) siftDown(a, i, a.size)
}
