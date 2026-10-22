package pricing

private const val BIG_ORDER_CENTS = 5000
private const val BIG_ORDER_MEMBER_PERCENT = 10
private const val MEMBER_PERCENT = 5
private const val COUPON_CAP_CENTS = 2000
private const val FREE_SHIPPING_FROM_CENTS = 3500
private const val SHIPPING_CENTS = 499

fun price(items: List<Pair<Int, Int>>, coupon: String?, member: Boolean): Int {
    var totalCents = 0
    for ((quantity, unitCents) in items) {
        if (quantity <= 0) continue
        totalCents += quantity * unitCents
    }
    if (member) {
        if (totalCents > BIG_ORDER_CENTS) {
            totalCents = totalCents - totalCents * BIG_ORDER_MEMBER_PERCENT / 100
        } else {
            totalCents = totalCents - totalCents * MEMBER_PERCENT / 100
        }
    }
    if (coupon != null) {
        if (coupon == "SAVE10") {
            var discount = totalCents * 10 / 100
            if (discount > COUPON_CAP_CENTS) discount = COUPON_CAP_CENTS
            totalCents = totalCents - discount
        } else if (coupon == "SAVE20") {
            var discount = totalCents * 20 / 100
            if (discount > COUPON_CAP_CENTS) discount = COUPON_CAP_CENTS
            totalCents = totalCents - discount
        } else if (coupon.startsWith("FLAT")) {
            val dollars = coupon.substring(4).toIntOrNull() ?: 0
            totalCents = totalCents - dollars * 100
            if (totalCents < 0) totalCents = 0
        }
    }
    if (totalCents < FREE_SHIPPING_FROM_CENTS) {
        if (!member) totalCents = totalCents + SHIPPING_CENTS
    }
    return totalCents
}
