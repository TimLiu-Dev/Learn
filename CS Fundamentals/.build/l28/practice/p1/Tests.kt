import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LawsTest {
    @Test fun `with 10 percent serial, 64 cores give under 9x`() {
        assertEquals(8.767, amdahlSpeedup(0.9, 64), 0.001)
    }

    @Test fun `one core or nothing parallel gives 1x, all parallel gives n`() {
        assertEquals("one core", 1.0, amdahlSpeedup(0.9, 1), 1e-9)
        assertEquals("nothing parallel", 1.0, amdahlSpeedup(0.0, 64), 1e-9)
        assertEquals("everything parallel", 64.0, amdahlSpeedup(1.0, 64), 1e-9)
    }

    @Test fun `impossible inputs are rejected`() {
        assertThrows("p = 1.5", IllegalArgumentException::class.java) { amdahlSpeedup(1.5, 4) }
        assertThrows("p = -0.1", IllegalArgumentException::class.java) { amdahlSpeedup(-0.1, 4) }
        assertThrows("n = 0", IllegalArgumentException::class.java) { amdahlSpeedup(0.5, 0) }
    }

    @Test fun `200 requests per second at 50 ms keep 10 threads busy`() {
        assertEquals(10, poolSizeFor(200, 50))
    }

    @Test fun `a fraction of a thread rounds up`() {
        assertEquals("30/s × 50 ms = 1.5 threads", 2, poolSizeFor(30, 50))
        assertEquals("1/s × 1 ms = 0.001 threads", 1, poolSizeFor(1, 1))
        assertEquals("no traffic", 0, poolSizeFor(0, 50))
    }

    @Test fun `big numbers don't overflow`() {
        assertEquals("50,000/s × 60 s", 3_000_000, poolSizeFor(50_000, 60_000))
    }
}
