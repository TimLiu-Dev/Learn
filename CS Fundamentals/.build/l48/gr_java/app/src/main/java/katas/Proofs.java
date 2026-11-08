package katas;

import java.math.BigInteger;

public final class Proofs {
    private Proofs() {}

    // The floor of the square root of n.
    // Invariant: lo * lo <= n < hi * hi
    //   Initialization: 0 * 0 <= n, and 46341^2 = 2,147,488,281 > Integer.MAX_VALUE >= n.
    //   Maintenance: mid replaces lo only if mid^2 <= n, and replaces hi only if mid^2 > n.
    //   Termination: hi - lo shrinks every pass. At the end hi == lo + 1, so lo^2 <= n < (lo+1)^2.
    public static int isqrt(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0, was " + n);
        long lo = 0, hi = 46_341;
        while (hi - lo > 1) {
            assert lo * lo <= n && n < hi * hi : "invariant broken: lo=" + lo + " hi=" + hi + " n=" + n;
            long mid = (lo + hi) / 2;
            if (mid * mid <= n) lo = mid; else hi = mid;
        }
        return (int) lo;
    }

    // base^exp mod m by square-and-multiply.
    // Invariant: (result * b^e) mod m == base^exp mod m
    //   Initialization: result = 1, b = base, e = exp.
    //   Maintenance: if e is odd, b^e = b * b^(e-1), so moving one b into result keeps the product;
    //                then b^e = (b^2)^(e/2) for even e.
    //   Termination: e halves every pass. At e == 0, b^e == 1, so result == base^exp mod m.
    public static long powMod(long base, long exp, long m) {
        if (exp < 0) throw new IllegalArgumentException("exp must be >= 0");
        if (m < 1 || m > 3_037_000_500L) throw new IllegalArgumentException("m out of range: (m-1)^2 must fit in a long");
        long result = 1 % m, b = Math.floorMod(base, m), e = exp;
        while (e > 0) {
            assert invariantHolds(base, exp, m, result, b, e) : "invariant broken: result=" + result + " b=" + b + " e=" + e;
            if (e % 2 == 1) result = result * b % m;
            b = b * b % m;
            e /= 2;
        }
        return result;
    }

    // Only called from assert, so it costs nothing unless assertions are enabled.
    private static boolean invariantHolds(long base, long exp, long m, long result, long b, long e) {
        BigInteger bigM = BigInteger.valueOf(m);
        BigInteger goal = BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), bigM);
        return BigInteger.valueOf(result).multiply(BigInteger.valueOf(b).modPow(BigInteger.valueOf(e), bigM)).mod(bigM).equals(goal);
    }
}
