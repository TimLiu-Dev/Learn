// Examples: 1234 -> 4, 7 -> 1, -907 -> 3, 0 -> 1, Int.MIN_VALUE -> 10
// Plan:
//   every number has at least one digit, so the count starts at 1
//   while dropping the last digit still leaves something, drop it and count one more
//   no abs(): dividing a negative number by 10 heads towards 0 as well
fun digitCount(number: Int): Int {
    var n = number
    var count = 1
    while (n / 10 != 0) {
        n /= 10
        count++
    }
    return count
}
