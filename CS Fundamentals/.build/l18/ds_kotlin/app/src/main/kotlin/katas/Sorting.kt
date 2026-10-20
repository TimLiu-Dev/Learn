package katas

import java.util.Collections

// O(n²) worst case, O(n) on sorted input, stable, in place.
fun <T : Comparable<T>> insertionSort(xs: MutableList<T>) {
    for (i in 1 until xs.size) {
        val key = xs[i]
        var j = i - 1
        while (j >= 0 && key < xs[j]) {
            xs[j + 1] = xs[j]
            j--
        }
        xs[j + 1] = key
    }
}

// O(n log n) always, stable, O(n) extra space (one aux buffer for the whole sort).
fun <T : Comparable<T>> mergeSort(xs: MutableList<T>) {
    val aux = ArrayList(xs)
    mergeSort(xs, aux, 0, xs.size)
}

private fun <T : Comparable<T>> mergeSort(xs: MutableList<T>, aux: MutableList<T>, lo: Int, hi: Int) {
    if (hi - lo < 2) return                  // half-open [lo, hi): 0 or 1 items
    val mid = lo + (hi - lo) / 2
    mergeSort(xs, aux, lo, mid)
    mergeSort(xs, aux, mid, hi)
    for (k in lo until hi) aux[k] = xs[k]
    var i = lo
    var j = mid
    for (k in lo until hi) {
        xs[k] = when {
            i >= mid -> aux[j++]             // left half used up
            j >= hi -> aux[i++]              // right half used up
            aux[j] < aux[i] -> aux[j++]      // strictly smaller: take right
            else -> aux[i++]                 // ties take left: stable
        }
    }
}

// O(n log n) on average, not stable, in place.
// Median-of-three pivot copes with sorted input; 3-way partition copes with duplicates.
fun <T : Comparable<T>> quickSort(xs: MutableList<T>) = quickSort(xs, 0, xs.size - 1)

private fun <T : Comparable<T>> quickSort(xs: MutableList<T>, lo: Int, hi: Int) {
    if (lo >= hi) return
    val pivot = medianOf(xs[lo], xs[lo + (hi - lo) / 2], xs[hi])
    var lt = lo                              // xs[lo..lt-1]  < pivot
    var i = lo                               // xs[lt..i-1]  == pivot
    var gt = hi                              // xs[gt+1..hi]  > pivot
    while (i <= gt) {
        val c = xs[i].compareTo(pivot)
        when {
            c < 0 -> Collections.swap(xs, lt++, i++)
            c > 0 -> Collections.swap(xs, i, gt--)
            else -> i++
        }
    }
    quickSort(xs, lo, lt - 1)
    quickSort(xs, gt + 1, hi)
}

private fun <T : Comparable<T>> medianOf(a: T, b: T, c: T): T = when {
    a < b -> if (b < c) b else if (a < c) c else a
    else -> if (a < c) a else if (b < c) c else b
}
