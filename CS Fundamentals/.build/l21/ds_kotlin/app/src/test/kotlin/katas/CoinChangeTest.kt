package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CoinChangeTest {
    @Test
    fun `classic example 11 from 1 2 5 needs 3 coins`() {
        assertEquals(3, minCoins(listOf(1, 2, 5), 11))
    }

    @Test
    fun `amount 0 needs no coins`() {
        assertEquals(0, minCoins(listOf(1, 2, 5), 0))
    }

    @Test
    fun `impossible amount gives -1`() {
        assertEquals(-1, minCoins(listOf(2), 3))
    }

    @Test
    fun `beats greedy on 1 3 4 for 6`() {
        assertEquals(2, minCoins(listOf(1, 3, 4), 6))   // 3 + 3; greedy says 4 + 1 + 1
    }

    @Test
    fun `large amount stays fast`() {
        assertEquals(20, minCoins(listOf(1, 5, 10, 25, 50), 1000))
    }

    @Test
    fun `negative amount is rejected`() {
        assertFailsWith<IllegalArgumentException> { minCoins(listOf(1), -1) }
    }
}
