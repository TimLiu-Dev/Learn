package katas

// Fewest coins that add up to amount, or -1 if no combination works.
fun minCoins(coins: List<Int>, amount: Int): Int {
    require(amount >= 0) { "amount must be >= 0, was $amount" }
    val none = Int.MAX_VALUE
    val dp = IntArray(amount + 1) { none }   // dp[a] = fewest coins that make a
    dp[0] = 0                                // base case: 0 needs no coins
    for (a in 1..amount) {
        for (coin in coins) {
            if (coin <= a && dp[a - coin] != none) {
                dp[a] = minOf(dp[a], dp[a - coin] + 1)   // use this coin last
            }
        }
    }
    return if (dp[amount] == none) -1 else dp[amount]
}
