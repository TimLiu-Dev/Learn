// One frame, however big n is: the loop reuses the same local variables.
fun sumTo(n: Int): Long {
    var total = 0L                  // Long: the sum outgrows an Int long before the loop ends
    for (i in 1..n) total += i      // 1..n is empty when n < 1, so the answer is 0
    return total
}
