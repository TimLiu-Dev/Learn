import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CanaryTest {
    private val users = (1..10_000).map { "user$it" }

    @Test fun `at 0 percent nobody gets the canary`() {
        assertEquals("users in the canary at 0%", 0, users.count { inCanary(it, 0) })
    }

    @Test fun `at 100 percent everybody does`() {
        assertEquals(users.size, users.count { inCanary(it, 100) })
    }

    @Test fun `about a tenth of users at 10 percent, the same ones every time`() {
        val first = users.filter { inCanary(it, 10) }
        assertTrue("expected 800..1200 users, got ${first.size}", first.size in 800..1200)
        assertEquals("second call", first, users.filter { inCanary(it, 10) })
    }

    @Test fun `raising the percent never moves a canary user back`() {
        assertTrue(users.filter { inCanary(it, 10) }.all { inCanary(it, 50) })
    }

    @Test fun `a user whose hashCode is Int MIN_VALUE stays out at 0 percent`() {
        assertEquals(Int.MIN_VALUE, "polygenelubricants".hashCode())
        assertFalse(inCanary("polygenelubricants", 0))
    }

    @Test fun `the verdict waits, rolls back, or promotes`() {
        val control = Stats(10_000, 40)                                    // 0.4% errors
        assertEquals(Verdict.WAIT, verdict(control, Stats(60, 12)))
        assertEquals(Verdict.ROLLBACK, verdict(control, Stats(200, 40)))  // 20%
        assertEquals(Verdict.PROMOTE, verdict(control, Stats(200, 1)))    // 0.5%
        assertEquals("exactly at the tolerance", Verdict.PROMOTE, verdict(Stats(1000, 0), Stats(100, 1)))
    }
}
