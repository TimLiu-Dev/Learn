// Every subset of nums (positive, distinct, sorted ascending) that sums to exactly target.
fun subsetSums(nums: List<Int>, target: Int): List<List<Int>> {
    val out = mutableListOf<List<Int>>()
    val current = mutableListOf<Int>()
    fun go(start: Int, sum: Int) {
        TODO("record a hit; else for each i from start: choose nums[i], explore, un-choose (and prune)")
    }
    go(0, 0)
    return out
}
