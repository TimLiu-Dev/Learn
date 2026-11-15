// Index of the first copy of target in the sorted list xs, or -1. O(log n).
fun firstIndexOf(xs: List<Int>, target: Int): Int {
    var lo = 0
    var hi = xs.size                 // [lo, hi): everything left of lo is < target, from hi on is >= target
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (xs[mid] < target) lo = mid + 1 else hi = mid
    }
    return if (lo < xs.size && xs[lo] == target) lo else -1
}

// Inserts x into the sorted list xs after any equal values; returns its index. O(log n) to find the spot.
fun insertSorted(xs: MutableList<Int>, x: Int): Int {
    var lo = 0
    var hi = xs.size                 // left of lo is <= x, from hi on is > x
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (xs[mid] <= x) lo = mid + 1 else hi = mid   // the one character that differs
    }
    xs.add(lo, x)
    return lo
}
