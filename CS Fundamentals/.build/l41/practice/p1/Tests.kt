import org.junit.Assert.assertEquals
import org.junit.Test

class PercentileTest {
    private val ten = doubleArrayOf(7.0, 2.0, 10.0, 4.0, 1.0, 9.0, 3.0, 8.0, 6.0, 5.0)

    @Test fun `with ten samples, p50 is the 5th smallest and p90 the 9th`() {
        assertEquals("p50", 5.0, percentile(ten, 50.0), 0.0)
        assertEquals("p90", 9.0, percentile(ten, 90.0), 0.0)
        assertEquals("p100 is the maximum", 10.0, percentile(ten, 100.0), 0.0)
    }

    @Test fun `a small sample rounds the rank up`() {
        val three = doubleArrayOf(30.0, 10.0, 20.0)
        assertEquals("p1 (rank 1)", 10.0, percentile(three, 1.0), 0.0)
        assertEquals("p50 (rank 2)", 20.0, percentile(three, 50.0), 0.0)
        assertEquals("p99 (rank 3)", 30.0, percentile(three, 99.0), 0.0)
    }

    @Test fun `ties, with one slow request in five`() {
        val ms = doubleArrayOf(5.0, 100.0, 5.0, 5.0, 5.0)
        assertEquals("p50", 5.0, percentile(ms, 50.0), 0.0)
        assertEquals("p80 (rank 4)", 5.0, percentile(ms, 80.0), 0.0)
        assertEquals("p81 (rank 5)", 100.0, percentile(ms, 81.0), 0.0)
    }

    @Test fun `the caller's array is not reordered`() {
        val ms = doubleArrayOf(3.0, 1.0, 2.0)
        percentile(ms, 50.0)
        assertEquals("array after the call", "[3.0, 1.0, 2.0]", ms.contentToString())
    }

    @Test fun `fan-out turns a rare slow call into a common slow page`() {
        assertEquals("1 call", 0.01, fanOutSlow(0.01, 1), 1e-9)
        assertEquals("10 calls", 0.0956, fanOutSlow(0.01, 10), 1e-4)
        assertEquals("100 calls", 0.634, fanOutSlow(0.01, 100), 1e-3)
    }
}
