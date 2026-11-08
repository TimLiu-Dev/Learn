import java.math.BigInteger

// Square-and-multiply: about 2 * log2(exp) multiplications instead of exp of them
fun powMod(base: Long, exp: Long, m: Long): Long {
    var result = 1L
    var b = base % m
    var e = exp
    while (e > 0) {
        if (e % 2 == 1L) result = result * b % m   // low bit of e is 1: multiply it in
        b = b * b % m                              // next bit is worth the square
        e /= 2
    }
    return result
}

fun main() {
    // Clock arithmetic: Wednesday (2) minus 10 days, with Monday = 0
    println("(2 - 10) % 7 = ${(2 - 10) % 7}   (2 - 10).mod(7) = ${(2 - 10).mod(7)}")

    // A negative hashCode breaks a naive bucket index
    val k = "android".hashCode()
    println("\"android\".hashCode() = $k")
    println("  % 10 = ${k % 10}   .mod(10) = ${k.mod(10)}   and 15 = ${k and 15}   .mod(16) = ${k.mod(16)}")
    // ...and abs() doesn't save you (L2): one hashCode has no positive twin
    val h = "polygenelubricants".hashCode()
    println("\"polygenelubricants\".hashCode() = $h   abs(h) % 10 = ${Math.abs(h) % 10}")

    // Modular exponentiation
    val m = 1_000_000_007L
    println("3^200 mod m  = ${powMod(3, 200, m)}   BigInteger: ${BigInteger.valueOf(3).modPow(BigInteger.valueOf(200), BigInteger.valueOf(m))}")
    println("3^(10^18) mod m = ${powMod(3, 1_000_000_000_000_000_000L, m)}   BigInteger: ${
        BigInteger.valueOf(3).modPow(BigInteger.TEN.pow(18), BigInteger.valueOf(m))}")
}
