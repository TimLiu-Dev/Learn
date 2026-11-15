import org.junit.Assert.assertEquals
import org.junit.Test

class EstimateTest {
    private val shortener = Assumptions(newPerMonth = 100e6, readsPerWrite = 100.0,
        bytesPerRow = 500.0, years = 5, hotFraction = 0.2)
    private val pastebin = Assumptions(newPerMonth = 10e6, readsPerWrite = 10.0,
        bytesPerRow = 10_000.0, years = 5, hotFraction = 0.2)

    @Test fun `the shortener's traffic per second`() {
        val e = estimate(shortener)
        assertEquals("writes per second", 38.58, e.writesPerSec, 0.01)
        assertEquals("reads per second", 3858.02, e.readsPerSec, 0.01)
    }

    @Test fun `the shortener's storage and cache in bytes`() {
        val e = estimate(shortener)
        assertEquals("storage bytes (3 TB)", 3.0e12, e.storageBytes, 1.0)
        assertEquals("cache bytes (10 GB)", 1.0e10, e.cacheBytes, 1.0)
    }

    @Test fun `the pastebin is a storage problem, not a traffic problem`() {
        val e = estimate(pastebin)
        assertEquals("reads per second", 38.58, e.readsPerSec, 0.01)
        assertEquals("storage bytes (6 TB)", 6.0e12, e.storageBytes, 1.0)
        assertEquals("cache bytes (20 GB)", 2.0e10, e.cacheBytes, 1.0)
    }

    @Test fun `code lengths at the boundaries`() {
        assertEquals("62 codes", 1, minCodeLength(62))
        assertEquals("63 codes", 2, minCodeLength(63))
        assertEquals("62^2 codes", 2, minCodeLength(3844))
        assertEquals("62^2 + 1 codes", 3, minCodeLength(3845))
        assertEquals("6 billion codes", 6, minCodeLength(6_000_000_000))
    }

    @Test fun `one link still needs one character`() {
        assertEquals(1, minCodeLength(1))
    }

    @Test(timeout = 2000) fun `every Long fits in 11 characters`() {
        assertEquals(11, minCodeLength(Long.MAX_VALUE))
    }
}
