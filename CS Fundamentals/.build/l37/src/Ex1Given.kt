fun t(p: List<Int>, m: Boolean): Int {
    var s = 0
    for (i in p) s += i       // add up
    if (m) s = s * 90 / 100   // 10%
    return s
}

fun main() {
    val prices = listOf(1299, 850, 2000)
    println("${t(prices, false)} ${t(prices, true)}")
}
