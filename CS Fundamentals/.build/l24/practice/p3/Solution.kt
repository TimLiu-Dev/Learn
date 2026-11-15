// Every Money is made through Money.of, which counts it: an exact allocation profile.
class Money private constructor(val cents: Long) {
    operator fun plus(other: Money) = of(cents + other.cents)
    operator fun times(qty: Int) = of(cents * qty)
    override fun equals(other: Any?) = other is Money && other.cents == cents
    override fun hashCode() = cents.hashCode()
    override fun toString() = "Money($cents)"

    companion object {
        var allocations = 0
        fun of(cents: Long): Money { allocations++; return Money(cents) }
    }
}

// Keep the running total in a primitive Long, and box it into a Money once, at the end.
fun total(prices: List<Money>, quantities: IntArray): Money {
    var cents = 0L
    for (i in prices.indices) cents += prices[i].cents * quantities[i]
    return Money.of(cents)
}
