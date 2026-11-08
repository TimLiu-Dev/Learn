import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyTest {
    @Test fun `of normalises the code`() {
        assertEquals("EUR", Currency.of(" eur ").code)
    }

    @Test fun `the same code gives the same object`() {
        assertSame("of(\"usd\") and of(\"USD\") should be one object", Currency.of("usd"), Currency.of("USD"))
    }

    @Test fun `different codes give different objects`() {
        assertNotSame(Currency.of("GBP"), Currency.of("JPY"))
    }

    @Test fun `bad codes are rejected`() {
        for (bad in listOf("EURO", "E1R", "")) {
            val e = runCatching { Currency.of(bad) }.exceptionOrNull()
            assertTrue("of(\"$bad\") should throw IllegalArgumentException, not $e", e is IllegalArgumentException)
        }
    }

    @Test fun `known counts distinct currencies`() {
        val before = Currency.known
        Currency.of("CHF")
        Currency.of("chf")
        assertEquals("CHF asked for twice should add 1", before + 1, Currency.known)
    }

    @Test fun `the constructor is private`() {
        // Kotlin adds a hidden synthetic constructor so the companion can reach the private one: skip it.
        val visible = Currency::class.java.constructors.filter { !it.isSynthetic }
        assertTrue("Currency still has a public constructor", visible.isEmpty())
    }
}
