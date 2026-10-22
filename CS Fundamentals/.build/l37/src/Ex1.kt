// The exercise snippet, as given:
fun t(p: List<Int>, m: Boolean): Int {
    var s = 0
    for (i in p) s += i       // add up
    if (m) s = s * 90 / 100   // 10%
    return s
}

const val MEMBER_PERCENT_OFF = 10

// First try: looks equivalent... (it is not: different rounding)
fun basketTotalCentsFirstTry(priceCents: List<Int>, isMember: Boolean): Int {
    val subtotal = priceCents.sum()
    return if (isMember) subtotal - subtotal * MEMBER_PERCENT_OFF / 100 else subtotal
}

/** Total of [priceCents] in cents; members pay (100 - [MEMBER_PERCENT_OFF])%, rounded down. */
fun basketTotalCents(priceCents: List<Int>, isMember: Boolean): Int {
    val subtotal = priceCents.sum()
    return if (isMember) subtotal * (100 - MEMBER_PERCENT_OFF) / 100 else subtotal
}

fun main() {
    val prices = listOf(1299, 850, 2000)
    for (member in listOf(false, true)) {
        println("member=$member  old=${t(prices, member)}  firstTry=${basketTotalCentsFirstTry(prices, member)}  new=${basketTotalCents(prices, isMember = member)}")
    }
}
