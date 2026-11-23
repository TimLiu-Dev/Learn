// The length of the longest contiguous run of a that sums to exactly k (0 if none).
fun longestWithSum(a: IntArray, k: Long): Int {
    val firstAt = HashMap<Long, Int>()                   // prefix sum -> the first index where it occurred
    var sum = 0L
    var best = 0
    for ((i, x) in a.withIndex()) {
        TODO("add x to sum, see whether sum - k occurred before, then remember where sum first occurred")
    }
    return best
}
