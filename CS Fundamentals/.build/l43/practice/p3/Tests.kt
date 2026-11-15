import org.junit.Assert.assertEquals
import org.junit.Test

class SelfTimeTest {
    // The lesson's trace (Part 3), listed child first.
    private val chain = listOf(
        Span("p", "b", "pricing", 19, 150),
        Span("g", null, "gateway", 0, 176),
        Span("b", "g", "books", 3, 175),
    )

    @Test fun `a leaf's self time is its whole duration`() {
        assertEquals(131L, selfTimes(chain)["p"])
    }

    @Test fun `a chain gives the lesson's waterfall`() {
        assertEquals(mapOf("g" to 4L, "b" to 41L, "p" to 131L), selfTimes(chain))
    }

    @Test fun `parallel children are counted once`() {
        val fanOut = listOf(
            Span("g", null, "gateway", 0, 100),
            Span("a", "g", "stock", 10, 60),
            Span("r", "g", "reviews", 20, 70),
        )
        assertEquals("children cover 10..70, so 60 ms", 40L, selfTimes(fanOut)["g"])
    }

    @Test fun `overlaps and gaps in any order`() {
        val spans = listOf(
            Span("c", "g", "search", 60, 90),
            Span("a", "g", "stock", 10, 40),
            Span("g", null, "gateway", 0, 100),
            Span("b", "g", "reviews", 30, 50),
        )
        assertEquals("children cover 10..50 and 60..90", 30L, selfTimes(spans)["g"])
    }

    @Test fun `the culprit is the service that spent the time itself`() {
        assertEquals("pricing", culprit(chain))
    }
}
