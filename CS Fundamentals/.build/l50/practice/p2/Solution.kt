// Every subset of nums (positive, distinct, sorted ascending) that sums to exactly target.
fun subsetSums(nums: List<Int>, target: Int): List<List<Int>> {
    val out = mutableListOf<List<Int>>()
    val current = mutableListOf<Int>()
    fun go(start: Int, sum: Int) {
        if (sum == target) { out += current.toList(); return }   // copy; any extension would overshoot
        for (i in start until nums.size) {
            if (sum + nums[i] > target) break            // prune: sorted, so every later number overshoots too
            current += nums[i]                           // choose
            go(i + 1, sum + nums[i])                     // explore: i + 1, so nums[i] is never reused
            current.removeLast()                         // un-choose
        }
    }
    go(0, 0)
    return out
}
