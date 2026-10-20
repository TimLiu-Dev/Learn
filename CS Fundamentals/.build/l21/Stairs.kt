// Ways to climb n stairs taking 1 or 2 steps at a time.
fun ways(n: Int): Long {
    val dp = LongArray(n + 1)          // 1. subproblem: dp[i] = ways to reach stair i
    dp[0] = 1                          // 3. base cases
    if (n >= 1) dp[1] = 1
    for (i in 2..n) {                  // 4. order: small to large
        dp[i] = dp[i - 1] + dp[i - 2]  // 2. recurrence: last step was 1 or 2
    }
    return dp[n]                       // 5. answer
}

fun main() {
    println((0..10).map(::ways))
    println("ways(50) = ${ways(50)}")
}
