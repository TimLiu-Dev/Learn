// Shortest contiguous run of POSITIVE numbers whose sum is at least target; 0 if none.
fun shortestAtLeast(a: IntArray, target: Int): Int {
    require(a.all { it > 0 }) { "all numbers must be positive" }
    var left = 0
    var sum = 0
    var best = Int.MAX_VALUE
    for (right in a.indices) {
        sum += a[right]                                  // grow on the right
        while (sum >= target) {                          // valid: record it, then try a shorter one
            best = minOf(best, right - left + 1)
            sum -= a[left++]
        }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}

fun main() {
    println(shortestAtLeast(intArrayOf(2, 3, 1, 2, 4, 3), 7))   // [4, 3]
    println(shortestAtLeast(intArrayOf(1, 4, 4), 4))            // [4]
    println(shortestAtLeast(intArrayOf(1, 1, 1), 11))           // impossible
}
