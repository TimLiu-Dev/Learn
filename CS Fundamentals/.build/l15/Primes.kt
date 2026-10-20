fun isPrime(n: Int): Boolean = n >= 2 && (2..n / 2).none { n % it == 0 }

fun main() {
    val primes = generateSequence(2) { it + 1 }.filter(::isPrime)   // infinite, lazy

    println(primes.take(10).toList())
    println(primes.first { it > 1_000 })
    println(primes.takeWhile { it < 50 }.count())
}
