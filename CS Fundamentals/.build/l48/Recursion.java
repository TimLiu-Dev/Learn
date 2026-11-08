import java.util.*;

public class Recursion {
    // Claim: sumTo(n) == n(n+1)/2 for every n >= 0.
    // Base case: sumTo(0) = 0 = 0*1/2.
    // Inductive step: if sumTo(n-1) == (n-1)n/2, then sumTo(n) = n + (n-1)n/2 = n(n+1)/2.
    static long sumTo(int n) { return n == 0 ? 0 : n + sumTo(n - 1); }

    // Strong induction: assume factor() is correct for EVERY number below n, not just n-1.
    static List<Integer> factor(int n) {
        if (n < 2) throw new IllegalArgumentException("n must be >= 2");
        for (int d = 2; d * d <= n; d++) {
            if (n % d == 0) {                                  // both parts are smaller than n
                List<Integer> result = new ArrayList<>(factor(d));
                result.addAll(factor(n / d));
                return result;
            }
        }
        return List.of(n);                                     // no divisor: n is prime
    }

    public static void main(String[] args) {
        System.out.println("sumTo(10) = " + sumTo(10) + ", formula = " + (10 * 11 / 2));
        System.out.println("factor(360) = " + factor(360));
        System.out.println("factor(97) = " + factor(97));
        System.out.println("factor(1001) = " + factor(1001));
    }
}
