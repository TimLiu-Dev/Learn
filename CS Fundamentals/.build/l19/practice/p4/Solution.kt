fun twoSum(nums: List<Int>, target: Int): Pair<Int, Int>? {
    val indexOf = HashMap<Int, Int>()                  // value -> an index where we saw it
    for ((j, x) in nums.withIndex()) {
        val i = indexOf[target - x]                    // look up the partner first...
        if (i != null) return i to j
        indexOf[x] = j                                 // ...then remember x, so x never pairs with itself
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
