fun twoSum(nums: List<Int>, target: Int): Pair<Int, Int>? {
    val indexOf = HashMap<Int, Int>()                  // value -> an index where we saw it
    for ((j, x) in nums.withIndex()) {
        indexOf[x] = j                                 // remember x first...
        val i = indexOf[target - x]                    // ...so x can pair with itself
        if (i != null) return i to j
    }
    return null
}

fun countPairs(nums: List<Int>, target: Int): Int {
    val seen = HashMap<Int, Int>()                     // value -> how many times so far
    var pairs = 0
    for (x in nums) {
        pairs += seen[target - x] ?: 0                 // every earlier partner makes one pair with x
        seen.merge(x, 1, Int::plus)
    }
    return pairs
}
