// Examples: 1234 -> 4321, 1200 -> 21, 7 -> 7, 0 -> 0
// Plan:
//   reversed starts at 0
//   while there are digits left:
//     take the last digit, append it to reversed (reversed * 10 + digit)
//     drop the last digit
fun reverseDigits(number: Int): Int {
    var n = number
    var reversed = 0
    while (n > 0) {
        reversed = reversed * 10 + n % 10
        n /= 10
    }
    return reversed
}

fun main() {
    println(reverseDigits(1234))
    println(reverseDigits(1200))
    println(reverseDigits(7))
    println(reverseDigits(0))
}
