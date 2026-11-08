fun sumTo(n: Int): Long {
    var total = 0
    for (i in 1..n) total += i
    return total.toLong()
}
