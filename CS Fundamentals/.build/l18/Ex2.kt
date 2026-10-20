// First index i with a[i] >= target, or a.size if there is none.
fun lowerBound(a: IntArray, target: Int): Int {
    var lo = 0
    var hi = a.size                  // half-open range [lo, hi): hi is a valid answer
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (a[mid] < target) lo = mid + 1 else hi = mid
    }
    return lo
}

// First index i with a[i] > target, or a.size if there is none.
fun upperBound(a: IntArray, target: Int): Int {
    var lo = 0
    var hi = a.size
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (a[mid] <= target) lo = mid + 1 else hi = mid
    }
    return lo
}

fun countOf(a: IntArray, x: Int) = upperBound(a, x) - lowerBound(a, x)

fun main() {
    val a = intArrayOf(1, 2, 2, 2, 3, 5, 8)
    println("lower(2)=${lowerBound(a, 2)} upper(2)=${upperBound(a, 2)} count(2)=${countOf(a, 2)}")
    println("lower(4)=${lowerBound(a, 4)} count(4)=${countOf(a, 4)}")
    println("lower(0)=${lowerBound(a, 0)} lower(9)=${lowerBound(a, 9)}")
    println("empty: ${lowerBound(intArrayOf(), 7)}")
}
