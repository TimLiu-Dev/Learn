// Before: the member discount, then the coupon, each on the running total.
fun before(cents: Int): Int {
    var total = cents
    total -= total * 10 / 100     // member: 10% off
    total -= total * 10 / 100     // coupon: 10% off what's left
    return total
}

// After an AI "simplification": both discounts in one step.
fun after(cents: Int): Int = cents - cents * (10 + 10) / 100

fun main() {
    println("${before(1000)} ${after(1000)}")
}
