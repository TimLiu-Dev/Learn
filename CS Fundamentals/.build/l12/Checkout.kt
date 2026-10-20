fun discountCents(priceCents: Long, percent: Int): Long {
    val rate = percent / 100
    return priceCents * rate
}

fun lineTotal(priceCents: Long, qty: Int, percentOff: Int): Long {
    val gross = priceCents * qty
    val discount = discountCents(gross, percentOff)
    return gross - discount
}

fun main() {
    val total = lineTotal(1000, 2, 10) + lineTotal(500, 2, 0)
    println("total: $total cents")   // expected 2800
}
