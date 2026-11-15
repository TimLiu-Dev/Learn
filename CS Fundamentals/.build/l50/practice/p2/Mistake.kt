// Every subset of nums (positive, distinct, sorted ascending) that sums to exactly target.
fun subsetSums(nums: List<Int>, target: Int): List<List<Int>> {
    val out = mutableListOf<List<Int>>()
    val current = mutableListOf<Int>()
    fun go(start: Int, sum: Int) {
        if (sum == target) { out += current.toList(); return }
        for (i in start until nums.size) {
            if (sum + nums[i] > target) break
            current += nums[i]
            go(i, sum + nums[i])                         // i, not i + 1: nums[i] can be picked again
            current.removeLast()
        }
    }
    go(0, 0)
    return out
}
