package katas

import java.math.BigInteger

// The floor of the square root of n.
// Invariant: lo * lo <= n < hi * hi
//   Initialization: 0 * 0 <= n, and 46341^2 = 2,147,488,281 > Int.MAX_VALUE >= n.
//   Maintenance: mid replaces lo only if mid^2 <= n, and replaces hi only if mid^2 > n.
//   Termination: hi - lo shrinks every pass. At the end hi == lo + 1, so lo^2 <= n < (lo+1)^2.
fun isqrt(n: Int): Int {
    require(n >= 0) { "n must be >= 0, was $n" }
    var lo = 0L
    var hi = 46_341L
    while (hi - lo > 1) {
        check(lo * lo <= n && n < hi * hi) { "invariant broken: lo=$lo hi=$hi n=$n" }
        val mid = (lo + hi) / 2
        if (mid * mid <= n) lo = mid else hi = mid
    }
    return lo.toInt()
}

// base^exp mod m by square-and-multiply.
// Invariant: (result * b^e) mod m == base^exp mod m
//   Initialization: result = 1, b = base, e = exp.
//   Maintenance: if e is odd, b^e = b * b^(e-1), so moving one b into result keeps the product;
//                then b^e = (b^2)^(e/2) for even e.
//   Termination: e halves every pass. At e == 0, b^e == 1, so result == base^exp mod m.
fun powMod(base: Long, exp: Long, m: Long): Long {
    require(exp >= 0) { "exp must be >= 0" }
    require(m in 1..3_037_000_500) { "m out of range: (m-1)^2 must fit in a Long" }
    val bigM = BigInteger.valueOf(m)
    val goal = BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), bigM)   // only for checking
    var result = 1 % m
    var b = base.mod(m)
    var e = exp
    while (e > 0) {
        check(BigInteger.valueOf(result) * BigInteger.valueOf(b).modPow(BigInteger.valueOf(e), bigM) % bigM == goal) {
            "invariant broken: result=$result b=$b e=$e"
        }
        if (e % 2 == 1L) result = result * b % m
        b = b * b % m
        e /= 2
    }
    return result
}
