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

// The hot loop. It's correct, but it makes two new Money objects per line of the basket.
fun total(prices: List<Money>, quantities: IntArray): Money {
    var sum = Money.of(0)
    for (i in prices.indices) sum += prices[i] * quantities[i]
    return sum
}
