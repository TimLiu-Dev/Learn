import org.junit.Assert.assertEquals
import org.junit.Test

class TrustTest {
    @Test fun `rare outages make most alerts false`() {
        assertEquals("1 outage minute in 10,000", 0.0098, pOutageGivenAlert(0.0001, 0.99, 0.01), 0.0001)
    }

    @Test fun `common outages make most alerts real`() {
        assertEquals("half the minutes are outages", 0.99, pOutageGivenAlert(0.5, 0.99, 0.01), 1e-9)
    }

    @Test fun `an alert that ignores the system tells you nothing`() {
        // Fires 30% of the time whatever happens, so it's independent of outages: P(outage | alert) = P(outage)
        assertEquals(0.02, pOutageGivenAlert(0.02, 0.3, 0.3), 1e-9)
    }

    @Test fun `identical timings have no spread`() {
        assertEquals(0.0, stdDev(listOf(41.5, 41.5, 41.5)), 1e-9)
    }

    @Test fun `standard deviation of a known sample`() {
        assertEquals("mean 5, variance 4", 2.0, stdDev(listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)), 1e-9)
    }

    @Test fun `shifting every timing does not change the spread`() {
        val xs = listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
        assertEquals(2.0, stdDev(xs.map { it + 100 }), 1e-9)
    }
}
