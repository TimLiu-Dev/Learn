// Claim: sumTo(n) == n(n+1)/2 for every n >= 0.
// Base case: sumTo(0) = 0 = 0*1/2.
// Inductive step: if sumTo(n-1) == (n-1)n/2, then sumTo(n) = n + (n-1)n/2 = n(n+1)/2.
fun sumTo(n: Int): Long = if (n == 0) 0 else n + sumTo(n - 1)

// Strong induction: assume factor() is correct for EVERY number below n, not just n-1.
fun factor(n: Int): List<Int> {
    require(n >= 2)
    var d = 2
    while (d * d <= n) {
        if (n % d == 0) return factor(d) + factor(n / d)   // both parts are smaller than n
        d++
    }
    return listOf(n)                                        // no divisor: n is prime
}

fun main() {
    println("sumTo(10) = ${sumTo(10)}, formula = ${10 * 11 / 2}")
    println("factor(360) = ${factor(360)}")
    println("factor(97) = ${factor(97)}")
    println("factor(1001) = ${factor(1001)}")
}
