// Bottom-up: dp[i] = ways to reach stair i.
fun ways(n: Int, steps: List<Int>): Long {
    val dp = LongArray(n + 1)
    dp[0] = 1                                   // one way to climb nothing: don't move
    for (i in 1..n) {
        for (s in steps) if (s <= i) dp[i] += dp[i - s]   // the last move was s
    }
    return dp[n]
}

// Top-down: the same recursion you'd write naively, plus a cache.
fun minCoins(coins: List<Int>, amount: Int): Int {
    val memo = HashMap<Int, Int>()
    fun best(a: Int): Int =
        if (a == 0) 0
        else memo.getOrPut(a) {                 // safe with recursion, unlike computeIfAbsent
            val options = coins.filter { it <= a }.map { best(a - it) }.filter { it >= 0 }
            if (options.isEmpty()) -1 else options.min() + 1
        }
    return best(amount)
}
