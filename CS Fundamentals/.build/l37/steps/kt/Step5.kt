package pricing

private const val BIG_ORDER_CENTS = 5000
private const val BIG_ORDER_MEMBER_PERCENT = 10
private const val MEMBER_PERCENT = 5
private const val COUPON_CAP_CENTS = 2000
private const val FREE_SHIPPING_FROM_CENTS = 3500
private const val SHIPPING_CENTS = 499

fun price(items: List<Pair<Int, Int>>, coupon: String?, member: Boolean): Int {
    var totalCents = subtotalCents(items)
    if (member) totalCents = percentOff(totalCents, memberDiscountPercent(totalCents))
    if (coupon != null) {
        if (coupon == "SAVE10") {
            totalCents = percentOff(totalCents, 10, COUPON_CAP_CENTS)
        } else if (coupon == "SAVE20") {
            totalCents = percentOff(totalCents, 20, COUPON_CAP_CENTS)
        } else if (coupon.startsWith("FLAT")) {
            val dollars = coupon.substring(4).toIntOrNull() ?: 0
            totalCents = totalCents - dollars * 100
            if (totalCents < 0) totalCents = 0
        }
    }
    return totalCents + shippingCents(totalCents, member)
}

private fun memberDiscountPercent(cents: Int) =
    if (cents > BIG_ORDER_CENTS) BIG_ORDER_MEMBER_PERCENT else MEMBER_PERCENT

private fun shippingCents(cents: Int, member: Boolean) =
    if (cents < FREE_SHIPPING_FROM_CENTS && !member) SHIPPING_CENTS else 0

private fun subtotalCents(items: List<Pair<Int, Int>>): Int {
    var cents = 0
    for ((quantity, unitCents) in items) {
        if (quantity <= 0) continue
        cents += quantity * unitCents
    }
    return cents
}

private fun percentOff(cents: Int, percent: Int, capCents: Int = Int.MAX_VALUE): Int =
    cents - minOf(cents * percent / 100, capCents)
