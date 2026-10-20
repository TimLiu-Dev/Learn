// A min-heap of Ints in a growable array. The root (smallest) is at index 0.
class IntMinHeap {
    private var a = IntArray(4)
    var size = 0
        private set

    private fun parent(i: Int) = (i - 1) / 2
    private fun left(i: Int) = 2 * i + 1             // right child is left(i) + 1

    private fun swap(i: Int, j: Int) { val t = a[i]; a[i] = a[j]; a[j] = t }

    fun add(x: Int) {
        if (size == a.size) a = a.copyOf(size * 2)   // grow like L16's MyArrayList
        a[size] = x                                  // 1. put it at the end (keeps the shape)
        var i = size++
        while (i > 0 && a[i] < a[parent(i)]) {       // 2. sift up while smaller than parent
            swap(i, parent(i))
            i = parent(i)
        }
    }

    fun peek(): Int {
        check(size > 0) { "heap is empty" }
        return a[0]
    }

    fun poll(): Int {
        val top = peek()
        a[0] = a[--size]                             // 1. move the LAST element to the root
        var i = 0
        while (true) {                               // 2. sift down: swap with the smaller child
            val l = left(i)
            if (l >= size) break                     // no children: done
            val child = if (l + 1 < size && a[l + 1] < a[l]) l + 1 else l
            if (a[i] <= a[child]) break              // already no bigger than both children
            swap(i, child)
            i = child
        }
        return top
    }

    override fun toString() = a.copyOf(size).joinToString(", ", "[", "]")
}

fun main() {
    val h = IntMinHeap()
    for (x in listOf(5, 1, 8, 3, 2, 9)) { h.add(x); println("add $x -> $h") }
    while (h.size > 0) { val top = h.poll(); println("poll $top -> $h") }
    try { h.poll() } catch (e: IllegalStateException) { println("poll on empty: ${e.message}") }
}
