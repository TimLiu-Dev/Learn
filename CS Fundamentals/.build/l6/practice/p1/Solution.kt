class Fraction(numerator: Int, denominator: Int = 1) {
    val num: Int
    val den: Int

    init {
        // Check first: init code runs top to bottom, and gcd(0, 0) is 0.
        require(denominator != 0) { "denominator must not be 0" }
        val g = gcd(numerator, denominator)
        val sign = if (denominator < 0) -1 else 1
        num = sign * numerator / g
        den = sign * denominator / g
    }

    constructor(text: String) : this(
        text.substringBefore('/').trim().toInt(),
        text.substringAfter('/').trim().toInt(),
    )
}

// Greatest common divisor, always >= 0. gcd(6, 8) == 2, gcd(0, 5) == 5.
fun gcd(a: Int, b: Int): Int = if (b == 0) kotlin.math.abs(a) else gcd(b, a % b)
