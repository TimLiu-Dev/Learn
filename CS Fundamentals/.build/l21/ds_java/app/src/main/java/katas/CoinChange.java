package katas;

import java.util.*;

public final class CoinChange {
    private CoinChange() {}

    // Fewest coins that add up to amount, or -1 if no combination works.
    public static int minCoins(List<Integer> coins, int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0, was " + amount);
        final int none = Integer.MAX_VALUE;
        int[] dp = new int[amount + 1];   // dp[a] = fewest coins that make a
        Arrays.fill(dp, none);
        dp[0] = 0;                        // base case: 0 needs no coins
        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a && dp[a - coin] != none) {
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);   // use this coin last
                }
            }
        }
        return dp[amount] == none ? -1 : dp[amount];
    }
}
