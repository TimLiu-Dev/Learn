import kotlin.math.abs

fun main() {
    println(Int.MAX_VALUE)          // 2147483647
    println(Int.MAX_VALUE + 1)      // wraps around
    println(Long.MAX_VALUE)
    println(-7 / 2)                 // truncates toward zero
    println(-7 % 2)                 // sign follows the left operand
    println((-7).mod(2))            // always non-negative for a positive divisor
    println(abs(Int.MIN_VALUE))     // there is no +2147483648 Int!
    println(0.1 + 0.2)
    println(1.0 / 0)                // Double division by zero
    println(0.0 / 0)
    println(Math.addExact(Int.MAX_VALUE, 1))   // throws instead of wrapping
}
