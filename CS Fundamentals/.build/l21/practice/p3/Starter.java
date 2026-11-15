import java.util.*;

public class Practice {
    static long ways(int n, List<Integer> steps) {
        throw new UnsupportedOperationException("TODO: dp[i] = ways to reach stair i; fill from 0 up to n");
    }

    static int minCoins(List<Integer> coins, int amount) {
        throw new UnsupportedOperationException("TODO: best(a) = 1 + the best of best(a - coin), cached");
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
