fun sumDigits(n: Int): Int =
    if (n < 10) n else n % 10 + sumDigits(n / 10)

fun isPalindrome(s: String): Boolean =
    if (s.length <= 1) true
    else s.first() == s.last() && isPalindrome(s.substring(1, s.length - 1))

var powCalls = 0

// x^n = (x^(n/2))^2, times x again if n is odd: halving, like binary search
fun fastPow(x: Long, n: Int): Long {
    powCalls++
    if (n == 0) return 1
    val half = fastPow(x, n / 2)
    return if (n % 2 == 0) half * half else half * half * x
}

fun main() {
    println("${sumDigits(1234)} ${sumDigits(7)}")
    println("${isPalindrome("racecar")} ${isPalindrome("rocket")} ${isPalindrome("")}")
    println("2^62 = ${fastPow(2, 62)} in $powCalls calls")
}
