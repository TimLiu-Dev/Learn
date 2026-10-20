package katas

// First index i with xs[i] >= target, or xs.size if there is none.
// xs must be sorted. O(log n).
fun <T : Comparable<T>> lowerBound(xs: List<T>, target: T): Int {
    var lo = 0
    var hi = xs.size                         // half-open [lo, hi)
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2         // no (lo + hi) overflow
        if (xs[mid] < target) lo = mid + 1 else hi = mid
    }
    return lo
}
