package katas

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WalletTest {
    private lateinit var wallet: Wallet

    @BeforeTest
    fun setUp() {
        wallet = Wallet()               // a fresh wallet before EVERY test
    }

    @Test
    fun `deposit increases the balance`() {
        wallet.deposit(500)
        assertEquals(500L, wallet.balance)
    }

    @Test
    fun `a new wallet is empty`() {
        assertEquals(0L, wallet.balance)   // unaffected by the test above
    }

    @Test
    fun `withdrawing too much is rejected`() {
        wallet.deposit(100)
        val e = assertFailsWith<IllegalArgumentException> {
            wallet.withdraw(500)
        }
        assertEquals("insufficient funds", e.message)
        assertEquals(100L, wallet.balance)  // and nothing changed
    }

    @Test
    fun `zero deposit is rejected`() {
        assertFailsWith<IllegalArgumentException> { wallet.deposit(0) }
    }
}
