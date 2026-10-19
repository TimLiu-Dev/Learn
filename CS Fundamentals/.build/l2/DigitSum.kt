import kotlin.math.abs

fun digitSum(number: Int): Int {
    // 1. Use the absolute value, so the sign doesn't matter
    var n = abs(number)
    var sum = 0
    // 2. Peel off the last digit until nothing is left
    while (n > 0) {
        sum += n % 10   // last digit
        n /= 10         // drop the last digit
    }
    return sum
}

fun main() {
    println(digitSum(123))           // expect 6
    println(digitSum(0))             // expect 0
    println(digitSum(-45))           // expect 9
    println(digitSum(Int.MIN_VALUE)) // expect 47 ... ?
}
