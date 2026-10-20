import java.util.*;

public class Stairs {
    // Ways to climb n stairs taking 1 or 2 steps at a time.
    static long ways(int n) {
        long[] dp = new long[n + 1];       // 1. subproblem: dp[i] = ways to reach stair i
        dp[0] = 1;                         // 3. base cases
        if (n >= 1) dp[1] = 1;
        for (int i = 2; i <= n; i++) {     // 4. order: small to large
            dp[i] = dp[i - 1] + dp[i - 2]; // 2. recurrence: last step was 1 or 2
        }
        return dp[n];                      // 5. answer
    }

    public static void main(String[] args) {
        List<Long> first = new ArrayList<>();
        for (int n = 0; n <= 10; n++) first.add(ways(n));
        System.out.println(first);
        System.out.println("ways(50) = " + ways(50));
    }
}
