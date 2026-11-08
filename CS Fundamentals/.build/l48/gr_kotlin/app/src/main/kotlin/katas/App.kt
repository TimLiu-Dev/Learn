package katas

fun main() {
    for (n in listOf(0, 15, 16, 1_000_000, Int.MAX_VALUE)) println("isqrt($n) = ${isqrt(n)}")
    println("2^10 mod 1000 = ${powMod(2, 10, 1000)}")
    println("3^(10^18) mod 1000000007 = ${powMod(3, 1_000_000_000_000_000_000L, 1_000_000_007)}")
}
