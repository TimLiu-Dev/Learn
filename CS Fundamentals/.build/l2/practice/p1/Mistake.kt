import kotlin.math.abs

// digitSum's loop, reused: abs() first, then peel digits while n > 0
fun digitCount(number: Int): Int {
    var n = abs(number)
    var count = 0
    while (n > 0) {
        n /= 10
        count++
    }
    return count
}
