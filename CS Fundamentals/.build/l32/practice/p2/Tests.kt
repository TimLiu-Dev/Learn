import org.junit.Assert.assertEquals
import org.junit.Test

class IdempotencyTest {
    @Test fun `a POST without a key creates every time`() {
        val shop = Orders()
        shop.post("Dune", null)
        shop.post("Dune", null)
        assertEquals("two plain POSTs, two orders", mapOf(1L to "Dune", 2L to "Dune"), shop.orders)
    }

    @Test fun `a retried POST with the same key creates one order`() {
        val shop = Orders()
        repeat(3) { shop.post("Dune", "k-1") }
        assertEquals("three tries with key k-1", mapOf(1L to "Dune"), shop.orders)
    }

    @Test fun `the retry gets the original response back`() {
        val shop = Orders()
        val first = shop.post("Dune", "k-1")
        val retry = shop.post("Dune", "k-1")
        assertEquals(Response(201, "order 1: Dune"), first)
        assertEquals("the replayed response", first, retry)
    }

    @Test fun `different keys are different orders, even for the same item`() {
        val shop = Orders()
        shop.post("Dune", "k-1")
        shop.post("Dune", "k-2")
        assertEquals("two keys, two intended orders", mapOf(1L to "Dune", 2L to "Dune"), shop.orders)
    }

    @Test fun `a key reused for a different item is refused with 422`() {
        val shop = Orders()
        shop.post("Dune", "k-1")
        val reused = shop.post("Emma", "k-1")
        assertEquals("status for a reused key", 422, reused.status)
        assertEquals("nothing new created", mapOf(1L to "Dune"), shop.orders)
    }

    @Test fun `a replayed PUT leaves the same state`() {
        val shop = Orders()
        val first = shop.put(42, "Dune")
        val again = shop.put(42, "Dune")
        assertEquals("first PUT creates", 201, first.status)
        assertEquals("the replay replaces", 200, again.status)
        assertEquals(mapOf(42L to "Dune"), shop.orders)
    }
}
