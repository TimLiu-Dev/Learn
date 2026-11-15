import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Fake clocks: real time passes with advance(); NTP steps only the wall clock.
class FakeClocks {
    var wall = 1_700_000_000_000L      // a time of day, in ms
    var mono = 42_000_000_000L         // an arbitrary starting point, in ns
    fun advance(ms: Long) { wall += ms; mono += ms * 1_000_000 }
    fun ntpStep(ms: Long) { wall += ms }
    fun lease(durationMs: Long) = Lease(durationMs, { wall }, { mono })
}

class LeaseTest {
    @Test fun `a lease is valid until its time runs out`() {
        val c = FakeClocks()
        val lease = c.lease(10_000)
        c.advance(9_999)
        assertTrue("after 9.999 s", lease.isValid())
        c.advance(1)
        assertFalse("after 10 s", lease.isValid())
    }

    @Test fun `remaining time is in milliseconds`() {
        val c = FakeClocks()
        val lease = c.lease(10_000)
        c.advance(2_500)
        assertEquals(7_500L, lease.remainingMs())
    }

    @Test fun `stepping the clock back does not stretch the lease`() {
        val c = FakeClocks()
        val lease = c.lease(10_000)
        c.advance(1_000)
        c.ntpStep(-2_000)
        c.advance(9_000)
        assertFalse("10 s of real time have passed, so the lease must be over", lease.isValid())
    }

    @Test fun `stepping the clock forward does not cut the lease short`() {
        val c = FakeClocks()
        val lease = c.lease(10_000)
        c.advance(1_000)
        c.ntpStep(3_600_000)
        assertEquals("1 s of real time has passed", 9_000L, lease.remainingMs())
    }

    @Test fun `the log line shows the time of day it runs out`() {
        val c = FakeClocks()
        val lease = c.lease(10_000)
        c.advance(4_000)
        c.ntpStep(-2_000)
        assertEquals("the wall clock now, plus the 6 s left", c.wall + 6_000, lease.expiresAtWall())
    }
}
