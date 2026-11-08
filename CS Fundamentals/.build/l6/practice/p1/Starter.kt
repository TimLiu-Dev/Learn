class Fraction(numerator: Int, denominator: Int = 1) {
    val num: Int = numerator      // TODO: lowest terms, and the sign lives here
    val den: Int = denominator    // TODO: lowest terms, and always positive

    // TODO: reject a zero denominator

    constructor(text: String) : this(TODO("split \"3/4\" at the slash"), 1)
}

// Greatest common divisor, always >= 0. gcd(6, 8) == 2, gcd(0, 5) == 5.
fun gcd(a: Int, b: Int): Int = if (b == 0) kotlin.math.abs(a) else gcd(b, a % b)
