fun swap(a: IntArray, i: Int, j: Int) { val t = a[i]; a[i] = a[j]; a[j] = t }

fun isMinHeap(a: IntArray): Boolean =
    TODO("every index i > 0 must be no smaller than its parent (i - 1) / 2")

fun siftDown(a: IntArray, i: Int, n: Int) {
    TODO("while a[i] has a child in the first n slots and is bigger than the smaller child, swap down")
}

fun heapify(a: IntArray) {
    TODO("siftDown every parent, from the last parent back to the root")
}
