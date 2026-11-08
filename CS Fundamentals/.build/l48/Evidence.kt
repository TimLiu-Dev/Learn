fun isPrime(n: Long): Boolean {
    if (n < 2) return false
    var d = 2L
    while (d * d <= n) { if (n % d == 0L) return false; d++ }
    return true
}

fun main() {
    // Claim A: 1 + 2 + ... + n == n(n+1)/2. Checked for n up to 100,000.
    var sum = 0L
    var allMatch = true
    for (n in 1..100_000L) { sum += n; if (sum != n * (n + 1) / 2) allMatch = false }
    println("Claim A holds for n = 1..100000: $allMatch")

    // Claim B: n*n + n + 41 is prime for every n >= 0. Check until it breaks.
    var n = 0L
    while (isPrime(n * n + n + 41)) n++
    println("Claim B holds for n = 0..${n - 1}, then fails: p($n) = ${n * n + n + 41} = 41 * 41")
}
