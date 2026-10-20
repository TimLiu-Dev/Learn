import java.util.*;

public class MemoTrap {
    static final Map<Integer, Long> memo = new HashMap<>();

    // Looks neat, but the mapping function modifies the map it is running inside.
    static long fib(int n) {
        if (n < 2) return n;
        return memo.computeIfAbsent(n, k -> fib(k - 1) + fib(k - 2));
    }

    public static void main(String[] args) {
        System.out.println(fib(30));
    }
}
