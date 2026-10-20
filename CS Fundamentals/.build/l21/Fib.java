import java.util.*;
import java.util.function.LongSupplier;

public class Fib {
    static long calls = 0;

    static long fibNaive(int n) {
        calls++;
        return n < 2 ? n : fibNaive(n - 1) + fibNaive(n - 2);
    }

    static final Map<Integer, Long> memo = new HashMap<>();

    // Top-down: same recursion, but each answer is computed once and cached.
    static long fibMemo(int n) {
        calls++;
        if (n < 2) return n;
        Long cached = memo.get(n);
        if (cached != null) return cached;
        long result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    // Bottom-up: fill a table from the smallest subproblem upwards. No recursion.
    static long fibTable(int n) {
        if (n < 2) return n;
        long[] table = new long[n + 1];
        table[1] = 1;
        for (int i = 2; i <= n; i++) table[i] = table[i - 1] + table[i - 2];
        return table[n];
    }

    // Each row only needs the previous two, so keep just those: O(1) space.
    static long fibTwoVars(int n) {
        long prev = 0, cur = 1;
        for (int i = 0; i < n; i++) {
            long next = prev + cur;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    static void timed(String label, LongSupplier block) {
        calls = 0;
        long t0 = System.nanoTime();
        long result = block.getAsLong();
        double ms = (System.nanoTime() - t0) / 1_000_000.0;
        System.out.printf("%-14s = %-20d calls=%-11d %8.2f ms%n", label, result, calls, ms);
    }

    public static void main(String[] args) {
        for (int n : new int[] {20, 30, 40}) timed("naive(" + n + ")", () -> fibNaive(n));
        timed("memo(40)", () -> fibMemo(40));
        timed("memo(90)", () -> { memo.clear(); return fibMemo(90); });
        timed("table(90)", () -> fibTable(90));
        timed("twoVars(90)", () -> fibTwoVars(90));
    }
}
