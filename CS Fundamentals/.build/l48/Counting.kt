import java.math.BigInteger

fun factorial(n: Int): Long = (1..n).fold(1L) { acc, k -> acc * k }

// C(n, k) without factorials: after step i, result == C(n, i + 1), so every division is exact.
fun choose(n: Int, k: Int): Long {
    var result = 1L
    for (i in 0 until k) result = result * (n - i) / (i + 1)
    return result
}

fun main() {
    // Product rule: independent choices multiply.
    println("4-digit PINs:        ${"%,d".format(10L * 10 * 10 * 10)}")
    println("8 lowercase letters: ${"%,d".format(Math.powExact(26L, 8))}")
    var wrapped = 1L
    repeat(12) { wrapped *= 62 }
    println("12 chars of [a-zA-Z0-9] as Long: $wrapped   <- overflowed")
    println("12 chars as BigInteger:          ${BigInteger.valueOf(62).pow(12)}")

    // Permutations and combinations
    println("20! = ${factorial(20)}")
    println("21! = ${factorial(21)}   <- overflowed")
    println("podium orders of 10 runners P(10,3) = ${10 * 9 * 8}, teams of 3 C(10,3) = ${choose(10, 3)}")
    println("poker hands C(52,5) = ${"%,d".format(choose(52, 5))}")
    println("C(60,30) = ${choose(60, 30)}, BigInteger check: ${
        (1..30).fold(BigInteger.ONE) { acc, i -> acc * BigInteger.valueOf(60L - i + 1) / BigInteger.valueOf(i.toLong()) }}")

    // Pigeonhole: more strings than Int values means some must share a hashCode
    println("\"Aa\".hashCode() = ${"Aa".hashCode()}, \"BB\".hashCode() = ${"BB".hashCode()}")
}
