import org.junit.Assert.assertEquals
import org.junit.Test

class PricedTest {
    @Test fun `a book has no tax`() {
        assertEquals("Dune: 10.0", Book("Dune", 10.0).label())
    }

    @Test fun `a gadget adds 20 percent`() {
        assertEquals(120.0, Gadget("Phone", 100.0).price, 1e-9)
    }

    @Test fun `labels works on a list of Priced`() {
        assertEquals(listOf("Dune: 10.0", "Phone: 120.0"), labels(listOf(Book("Dune", 10.0), Gadget("Phone", 100.0))))
    }

    @Test fun `anything Priced gets the default label`() {
        val giftCard = object : Priced {
            override val name = "Gift card"
            override val price = 25.0
        }
        assertEquals(listOf("Gift card: 25.0"), labels(listOf(giftCard)))
    }
}
