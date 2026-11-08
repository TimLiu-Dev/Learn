// Slow but obviously correct: try every candidate divisor.
fun gcdSlow(a: Int, b: Int): Int = (maxOf(a, b) downTo 1).first { a % it == 0 && b % it == 0 }

// Euclid as a state machine: (x, y) -> (y, x % y). Invariant: gcd(x, y) == gcd(a, b).
fun gcd(a: Int, b: Int): Int {
    require(a >= 0 && b >= 0 && (a > 0 || b > 0))
    val target = gcdSlow(a, b)                  // only for checking the invariant
    var x = a
    var y = b
    var steps = 0
    while (y != 0) {
        check(gcdSlow(x, y) == target) { "invariant broken at ($x, $y)" }
        val r = x % y
        x = y
        y = r
        steps++
    }
    // y == 0, so gcd(x, 0) == x, and the invariant says that equals gcd(a, b).
    println("gcd($a, $b) = $x in $steps steps")
    return x
}

fun main() {
    gcd(1071, 462)
    gcd(462, 1071)        // first step just swaps them
    gcd(17, 0)
    gcd(832040, 514229)   // consecutive Fibonacci numbers: many steps for their size
}
