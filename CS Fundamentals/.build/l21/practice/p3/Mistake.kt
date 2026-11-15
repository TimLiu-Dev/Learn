fun ways(n: Int, steps: List<Int>): Long {
    val dp = LongArray(n + 1)
    dp[0] = 1
    for (i in 1..n) {
        for (s in steps) if (s <= i) dp[i] += dp[i - s]
    }
    return dp[n]
}

// Greedy: always take the biggest coin that still fits.
fun minCoins(coins: List<Int>, amount: Int): Int {
    var left = amount
    var count = 0
    for (coin in coins.sortedDescending()) {
        count += left / coin
        left %= coin
    }
    return if (left == 0) count else -1
}
