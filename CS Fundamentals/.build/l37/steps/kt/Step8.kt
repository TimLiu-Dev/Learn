package pricing

private const val BIG_ORDER_CENTS = 5000
private const val BIG_ORDER_MEMBER_PERCENT = 10
private const val MEMBER_PERCENT = 5
private const val COUPON_CAP_CENTS = 2000
private const val FREE_SHIPPING_FROM_CENTS = 3500
private const val SHIPPING_CENTS = 499

sealed interface Coupon {
    fun apply(totalCents: Int): Int

    data class PercentOff(val percent: Int) : Coupon {
        override fun apply(totalCents: Int) = percentOff(totalCents, percent, COUPON_CAP_CENTS)
    }

    data class FlatOff(val cents: Int) : Coupon {
        override fun apply(totalCents: Int) = maxOf(0, totalCents - cents)
    }

    data object None : Coupon {
        override fun apply(totalCents: Int) = totalCents
    }

    companion object {
        fun parse(code: String?): Coupon = when {
            code == "SAVE10" -> PercentOff(10)
            code == "SAVE20" -> PercentOff(20)
            code != null && code.startsWith("FLAT") -> FlatOff((code.substring(4).toIntOrNull() ?: 0) * 100)
            else -> None
        }
    }
}

data class Line(val quantity: Int, val unitCents: Int)

data class Order(val lines: List<Line>, val coupon: Coupon, val isMember: Boolean)

fun price(items: List<Pair<Int, Int>>, coupon: String?, member: Boolean): Int =
    price(parseOrder(items, coupon, member))

// Phase 1: raw input -> clean model. Bad quantities are dropped here, once.
fun parseOrder(items: List<Pair<Int, Int>>, couponCode: String?, isMember: Boolean): Order =
    Order(
        lines = items.filter { (quantity, _) -> quantity > 0 }.map { (quantity, unitCents) -> Line(quantity, unitCents) },
        coupon = Coupon.parse(couponCode),
        isMember = isMember,
    )

// Phase 2: arithmetic on the clean model. No strings, no validation.
fun price(order: Order): Int {
    var totalCents = order.lines.sumOf { it.quantity * it.unitCents }
    if (order.isMember) totalCents = percentOff(totalCents, memberDiscountPercent(totalCents))
    totalCents = order.coupon.apply(totalCents)
    return totalCents + shippingCents(totalCents, order.isMember)
}

private fun memberDiscountPercent(cents: Int) =
    if (cents > BIG_ORDER_CENTS) BIG_ORDER_MEMBER_PERCENT else MEMBER_PERCENT

private fun shippingCents(cents: Int, isMember: Boolean) =
    if (cents < FREE_SHIPPING_FROM_CENTS && !isMember) SHIPPING_CENTS else 0

private fun percentOff(cents: Int, percent: Int, capCents: Int = Int.MAX_VALUE): Int =
    cents - minOf(cents * percent / 100, capCents)
