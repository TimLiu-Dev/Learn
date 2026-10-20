var trace = false

fun binarySearch(a: IntArray, target: Int): Int {
    var lo = 0
    var hi = a.size - 1
    // Invariant: if target is anywhere in a, it is in a[lo..hi].
    while (lo <= hi) {
        val mid = lo + (hi - lo) / 2
        if (trace) println("  lo=$lo mid=$mid hi=$hi  a[mid]=${a[mid]}")
        when {
            a[mid] < target -> lo = mid + 1      // target is right of mid
            a[mid] > target -> hi = mid - 1      // target is left of mid
            else -> return mid
        }
    }
    return -1                                    // lo > hi: the range is empty
}

fun binarySearchRec(a: IntArray, target: Int, lo: Int = 0, hi: Int = a.size - 1): Int {
    if (lo > hi) return -1                       // base case: empty range
    val mid = lo + (hi - lo) / 2
    return when {
        a[mid] < target -> binarySearchRec(a, target, mid + 1, hi)
        a[mid] > target -> binarySearchRec(a, target, lo, mid - 1)
        else -> mid
    }
}

fun main() {
    val a = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31)
    trace = true
    println("search 23:"); println("-> ${binarySearch(a, 23)}")
    println("search 4:");  println("-> ${binarySearch(a, 4)}")
    trace = false
    println("recursive: ${binarySearchRec(a, 23)} ${binarySearchRec(a, 4)}")
}
