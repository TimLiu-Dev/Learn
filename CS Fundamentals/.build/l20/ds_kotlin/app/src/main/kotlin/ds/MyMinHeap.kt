package ds

// A min-heap in a growable array, like java.util.PriorityQueue (natural ordering).
class MyMinHeap<T : Comparable<T>> {
    private var a = arrayOfNulls<Any?>(4)
    var size = 0
        private set

    @Suppress("UNCHECKED_CAST")
    private fun at(i: Int): T = a[i] as T              // erased: the cast is unchecked (L13)

    private fun swap(i: Int, j: Int) { val t = a[i]; a[i] = a[j]; a[j] = t }

    fun add(x: T) {
        if (size == a.size) a = a.copyOf(size * 2)
        a[size] = x
        var i = size++
        while (i > 0 && at(i) < at((i - 1) / 2)) {     // sift up
            swap(i, (i - 1) / 2)
            i = (i - 1) / 2
        }
    }

    // Like PriorityQueue: null when empty instead of throwing.
    fun peek(): T? = if (size == 0) null else at(0)

    fun poll(): T? {
        val top = peek() ?: return null
        a[0] = a[--size]
        a[size] = null                                 // don't keep a stale reference (L4/L24)
        var i = 0
        while (true) {                                 // sift down
            val l = 2 * i + 1
            if (l >= size) break
            val child = if (l + 1 < size && at(l + 1) < at(l)) l + 1 else l
            if (at(i) <= at(child)) break
            swap(i, child)
            i = child
        }
        return top
    }
}
