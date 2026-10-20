// Pick a pivot, partition: smaller items left of it, the rest right. Recurse on both sides.
fun quickSort(a: IntArray, lo: Int = 0, hi: Int = a.size - 1) {
    if (lo >= hi) return
    val p = partition(a, lo, hi)                 // a[p] is now in its final place
    quickSort(a, lo, p - 1)
    quickSort(a, p + 1, hi)
}

// Lomuto partition with the naive choice: the LAST element is the pivot.
fun partition(a: IntArray, lo: Int, hi: Int): Int {
    val pivot = a[hi]
    var i = lo                                   // a[lo..i-1] < pivot
    for (j in lo until hi) {
        if (less(a[j], pivot)) { a.swap(i, j); i++ }
    }
    a.swap(i, hi)                                // pivot goes between the two parts
    return i
}
