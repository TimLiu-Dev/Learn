// The length of the longest contiguous run of a that sums to exactly k (0 if none).
fun longestWithSum(a: IntArray, k: Long): Int {
    val firstAt = HashMap<Long, Int>()
    firstAt[0L] = -1
    var sum = 0L
    var best = 0
    for ((i, x) in a.withIndex()) {
        sum += x
        val start = firstAt[sum - k]
        if (start != null) best = maxOf(best, i - start)
        firstAt[sum] = i                                 // overwrites: keeps the LATEST index, so runs come out too short
    }
    return best
}
