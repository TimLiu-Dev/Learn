// The length of the longest contiguous run of a that sums to exactly k (0 if none).
fun longestWithSum(a: IntArray, k: Long): Int {
    val firstAt = HashMap<Long, Int>()                   // prefix sum -> the first index where it occurred
    firstAt[0L] = -1                                     // the empty prefix "ends" just before index 0
    var sum = 0L
    var best = 0
    for ((i, x) in a.withIndex()) {
        sum += x                                         // sum of a[0..i]
        val start = firstAt[sum - k]                     // a run (start, i] sums to k
        if (start != null) best = maxOf(best, i - start)
        firstAt.putIfAbsent(sum, i)                      // keep the FIRST index: it gives the longest run
    }
    return best
}
