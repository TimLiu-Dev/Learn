package shop

// STRATEGY: a pricing rule is a function from (order, running total) to a new total
typealias PricingRule = (Order, Long) -> Long

val memberDiscount: PricingRule = { order, total -> if (order.member) total * 90 / 100 else total }
val bulkDiscount: PricingRule = { order, total -> if (order.itemCount >= 10) total - 500 else total }
val couponSave5: PricingRule = { order, total -> if (order.coupon == "SAVE5") maxOf(0, total - 500) else total }

val standardRules = listOf(memberDiscount, bulkDiscount, couponSave5)

// The context applies whatever rules it is given, in order (fold: L14)
fun price(order: Order, rules: List<PricingRule>): Long =
    rules.fold(order.subtotalCents) { total, rule -> rule(order, total) }

fun cents(c: Long) = "$%d.%02d".format(c / 100, c % 100)
