// Find any copy with a classic binary search, then walk left to the first one.
fun firstIndexOf(xs: List<Int>, target: Int): Int {
    var lo = 0
    var hi = xs.size - 1
    while (lo <= hi) {
        val mid = lo + (hi - lo) / 2
        when {
            xs[mid] < target -> lo = mid + 1
            xs[mid] > target -> hi = mid - 1
            else -> {
                var i = mid
                while (i > 0 && xs[i - 1] == target) i--   // O(n) when there are many copies
                return i
            }
        }
    }
    return -1
}

fun insertSorted(xs: MutableList<Int>, x: Int): Int {
    var lo = 0
    var hi = xs.size
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (xs[mid] <= x) lo = mid + 1 else hi = mid
    }
    xs.add(lo, x)
    return lo
}
