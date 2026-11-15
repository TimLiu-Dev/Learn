// One item (weight 2, value 3) and capacity 6. Only the loop direction differs.
fun best(highToLow: Boolean): Int {
    val w = 2; val v = 3; val cap = 6
    val best = IntArray(cap + 1)
    val order = if (highToLow) cap downTo w else w..cap
    for (c in order) best[c] = maxOf(best[c], best[c - w] + v)
    return best[cap]
}

fun main() = println("${best(highToLow = true)} ${best(highToLow = false)}")
