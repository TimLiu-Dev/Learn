import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FencedStoreTest {
    @Test fun `the paused holder's late write is rejected`() {
        val store = FencedStore()
        assertTrue("client1 writes with token 33 while its lease is valid", store.write("report", "v1", 33))
        // client1 pauses, its lease expires, client2 gets token 34
        assertTrue("client2 writes with token 34", store.write("report", "v2", 34))
        assertFalse("client1 wakes up and writes with token 33", store.write("report", "v1-late", 33))
        assertEquals("client2's value must survive", "v2", store.read("report"))
    }

    @Test fun `the current holder can write several times with one token`() {
        val store = FencedStore()
        assertTrue(store.write("report", "draft", 34))
        assertTrue("same token, same holder: must be accepted", store.write("report", "final", 34))
        assertEquals("final", store.read("report"))
    }

    @Test fun `a rejected write changes nothing, even when it is retried`() {
        val store = FencedStore()
        store.write("report", "v2", 34)
        assertFalse(store.write("report", "stale", 33))
        assertFalse("a retry with the same stale token must be rejected again", store.write("report", "stale", 33))
        assertEquals("v2", store.read("report"))
    }

    @Test fun `a newer holder still gets through after a rejection`() {
        val store = FencedStore()
        store.write("report", "v2", 34)
        store.write("report", "stale", 33)
        assertTrue("token 35 is newer than everything seen", store.write("report", "v3", 35))
        assertEquals("v3", store.read("report"))
    }

    @Test fun `each key is fenced on its own`() {
        val store = FencedStore()
        assertTrue(store.write("invoices", "run 50", 50))
        assertTrue("another lock's token 7 must not be compared with 50", store.write("emails", "run 7", 7))
        assertEquals("run 7", store.read("emails"))
        assertNull("a key nobody wrote", store.read("payroll"))
    }
}
