import org.junit.Assert.assertEquals
import org.junit.Test

class CoinTest {
    @Test fun `total adds up each coin's cents`() {
        assertEquals(37, total(listOf(Coin.QUARTER, Coin.DIME, Coin.PENNY, Coin.PENNY)))
        assertEquals("no coins", 0, total(emptyList()))
    }

    @Test fun `41 cents is one of each coin, largest first`() {
        assertEquals(listOf(Coin.QUARTER, Coin.DIME, Coin.NICKEL, Coin.PENNY), change(41))
    }

    @Test fun `30 cents is a quarter and a nickel`() {
        assertEquals(listOf(Coin.QUARTER, Coin.NICKEL), change(30))
    }

    @Test fun `zero cents needs no coins`() {
        assertEquals(emptyList<Coin>(), change(0))
    }

    @Test fun `change always adds back up to the amount`() {
        for (amount in 0..99) assertEquals("total(change($amount))", amount, total(change(amount)))
    }
}
