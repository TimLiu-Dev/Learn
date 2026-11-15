import org.junit.Assert.assertEquals
import org.junit.Test

fun basket(n: Int): Pair<List<Money>, IntArray> =
    List(n) { Money.of(100L + it % 7) } to IntArray(n) { 1 + it % 3 }

class AllocationTest {
    @Test fun `a small basket totals correctly`() {
        val prices = listOf(Money.of(250), Money.of(199), Money.of(1000))
        assertEquals(Money.of(250 * 2 + 199 * 3 + 1000), total(prices, intArrayOf(2, 3, 1)))
    }

    @Test fun `one call allocates exactly one Money, however long the basket`() {
        val (prices, quantities) = basket(10_000)
        val before = Money.allocations
        total(prices, quantities)
        assertEquals("Money objects allocated by one call to total", 1, Money.allocations - before)
    }

    @Test fun `an empty basket is zero, and still one allocation`() {
        val before = Money.allocations
        val result = total(listOf(), IntArray(0))
        assertEquals("Money objects allocated", 1, Money.allocations - before)
        assertEquals(Money.of(0), result)
    }

    @Test fun `the new total matches the original loop on a big basket`() {
        val (prices, quantities) = basket(10_000)
        var original = Money.of(0)
        for (i in prices.indices) original += prices[i] * quantities[i]
        assertEquals(original, total(prices, quantities))
    }
}
