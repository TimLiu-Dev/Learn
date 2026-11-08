// Pair-sum on a SORTED array, and in-place dedupe: two indices instead of two nested loops.

// Brute force: try every pair. O(n²) comparisons.
fun pairBrute(a: IntArray, target: Int): Pair<Int, Int>? {
    for (i in a.indices)
        for (j in i + 1 until a.size)
            if (a[i] + a[j] == target) return i to j
    return null
}

// Two pointers: start at both ends and move one of them inward each step. O(n).
fun pairTwoPointers(a: IntArray, target: Int, trace: Boolean = false): Pair<Int, Int>? {
    var lo = 0
    var hi = a.size - 1
    while (lo < hi) {
        val sum = a[lo] + a[hi]
        if (trace) println("  lo=$lo hi=$hi  ${a[lo]} + ${a[hi]} = $sum")
        when {
            sum == target -> return lo to hi
            sum < target -> lo++          // a[lo] is too small to pair with anything left
            else -> hi--                  // a[hi] is too big to pair with anything left
        }
    }
    return null
}

// Remove duplicates from a sorted array in place; returns the new logical length.
// slow = where the next unique value goes; fast = the value being examined.
fun dedupe(a: IntArray): Int {
    if (a.isEmpty()) return 0
    var slow = 1
    for (fast in 1 until a.size) {
        if (a[fast] != a[slow - 1]) a[slow++] = a[fast]
    }
    return slow
}

fun main() {
    val a = intArrayOf(1, 3, 4, 6, 8, 11, 15)
    println("find 14:")
    println("two pointers -> ${pairTwoPointers(a, 14, trace = true)}")
    println("brute force  -> ${pairBrute(a, 14)}")
    println("find 13 -> ${pairTwoPointers(a, 13)}")

    val d = intArrayOf(1, 1, 2, 3, 3, 3, 7, 9, 9)
    val n = dedupe(d)
    println("dedupe -> length $n: ${d.copyOf(n).contentToString()}  (whole array: ${d.contentToString()})")
}
