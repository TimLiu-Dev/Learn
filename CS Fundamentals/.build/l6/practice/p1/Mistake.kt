class Fraction(numerator: Int, denominator: Int = 1) {
    private val g = gcd(numerator, denominator)
    private val sign = if (denominator < 0) -1 else 1
    val num: Int = sign * numerator / g
    val den: Int = sign * denominator / g

    init {
        require(denominator != 0) { "denominator must not be 0" }   // too late
    }

    constructor(text: String) : this(
        text.substringBefore('/').trim().toInt(),
        text.substringAfter('/').trim().toInt(),
    )
}

fun gcd(a: Int, b: Int): Int = if (b == 0) kotlin.math.abs(a) else gcd(b, a % b)
