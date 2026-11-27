import org.junit.Assert.assertEquals
import org.junit.Test

class HeapTest {
    private fun mb(n: Long) = n * MB
    private fun heapMb(limitMb: Long, xmxMb: Long? = null, pct: Double = 25.0) =
        defaultMaxHeap(mb(limitMb), xmxMb?.let { mb(it) }, pct) / MB

    @Test fun `roundUp keeps multiples and lifts the rest`() {
        assertEquals(mb(256), roundUp(mb(256), ALIGN))
        assertEquals(mb(152), roundUp(mb(150), ALIGN))
        assertEquals(mb(8), roundUp(1, ALIGN))
    }

    @Test fun `big limits get 25 percent`() {
        assertEquals("1 GB limit", 256L, heapMb(1024))
        assertEquals("4 GB limit", 1024L, heapMb(4096))
        assertEquals("3000 MB limit: 750 MB, rounded up", 752L, heapMb(3000))
    }

    @Test fun `tiny limits get 50 percent`() {
        assertEquals("128 MB limit", 64L, heapMb(128))
        assertEquals("200 MB limit: 100 MB, rounded up", 104L, heapMb(200))
    }

    @Test fun `in between, the heap never drops below about 125 MB`() {
        assertEquals("400 MB limit: 25% would be only 100 MB", 128L, heapMb(400))
        assertEquals("512 MB limit", 128L, heapMb(512))
    }

    @Test fun `MaxRAMPercentage replaces the 25`() {
        assertEquals("2 GB limit, 75%", 1536L, heapMb(2048, pct = 75.0))
    }

    @Test fun `Xmx beats every percentage`() {
        assertEquals("2 GB limit, 75%, -Xmx300m", 304L, heapMb(2048, xmxMb = 300, pct = 75.0))
    }
}
