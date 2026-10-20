data class Money(val cents: Long, val currency: String)

fun shout(s: String): String = s.uppercase()

fun countEvens(xs: IntArray): Int {
    var n = 0
    xs.forEach { if (it % 2 == 0) n++ }
    return n
}

fun size(code: Int): String = when (code) {
    0 -> "S"
    1 -> "M"
    2 -> "L"
    3 -> "XL"
    else -> "?"
}

fun main() {
    println(shout("case closed"))
    println(countEvens(intArrayOf(1, 2, 3, 4, 6)))
    println(size(2) + " " + size(9))
    println(Money(500, "EUR") == Money(500, "EUR"))
}
