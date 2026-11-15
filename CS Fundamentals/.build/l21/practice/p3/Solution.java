import java.util.*;

public class Practice {
    // Bottom-up: dp[i] = ways to reach stair i.
    static long ways(int n, List<Integer> steps) {
        long[] dp = new long[n + 1];
        dp[0] = 1;                                  // one way to climb nothing: don't move
        for (int i = 1; i <= n; i++) {
            for (int s : steps) if (s <= i) dp[i] += dp[i - s];   // the last move was s
        }
        return dp[n];
    }

    // Top-down: the naive recursion plus a cache.
    static int minCoins(List<Integer> coins, int amount) {
        return best(coins, amount, new HashMap<>());
    }

    static int best(List<Integer> coins, int a, Map<Integer, Integer> memo) {
        if (a == 0) return 0;
        Integer cached = memo.get(a);               // get, compute, put: not computeIfAbsent
        if (cached != null) return cached;
        int result = -1;
        for (int coin : coins) {
            if (coin > a) continue;
            int rest = best(coins, a - coin, memo);
            if (rest >= 0 && (result < 0 || rest + 1 < result)) result = rest + 1;
        }
        memo.put(a, result);
        return result;
    }

    // CHECKS
    public static void main(String[] args) {
        check("10 stairs by 1 or 2 has 89 ways", 89L, () -> ways(10, List.of(1, 2)));
        check("edge cases for stairs", "1 0", () -> ways(0, List.of(1, 2)) + " " + ways(7, List.of(2)));
        check("big answers fit in a long", 37889062373143906L, () -> ways(80, List.of(1, 2)));
        check("DP beats greedy on 1 3 4 for 6", 2, () -> minCoins(List.of(1, 3, 4), 6));
        check("zero needs no coins and an impossible amount gives -1", "0 -1", () -> minCoins(List.of(5, 10), 0) + " " + minCoins(List.of(2), 3));
        check("the memo keeps a larger amount fast", 9, () -> minCoins(List.of(1, 5, 10, 25), 99));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
