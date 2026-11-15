import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BreakerTest {
    private var t = 0L
    private val breaker = Breaker(threshold = 3, openMs = 1000) { t }

    private fun fail(times: Int) = repeat(times) {
        assertTrue("a closed circuit lets calls through", breaker.allowRequest())
        breaker.recordFailure()
    }

    @Test fun `three failures in a row open the circuit`() {
        fail(2)
        assertEquals("after 2 failures", State.CLOSED, breaker.state)
        fail(1)
        assertEquals("after 3 failures", State.OPEN, breaker.state)
        assertFalse("an open circuit must fail fast", breaker.allowRequest())
    }

    @Test fun `a success resets the count`() {
        fail(2)
        breaker.allowRequest()
        breaker.recordSuccess()
        fail(2)
        assertEquals("only 2 failures in a row since the success", State.CLOSED, breaker.state)
    }

    @Test fun `it stays open until openMs have passed`() {
        t = 5000
        fail(3)
        t = 5999
        assertFalse("999 ms after opening", breaker.allowRequest())
        t = 6000
        assertTrue("1000 ms after opening, one trial may go", breaker.allowRequest())
        assertEquals(State.HALF_OPEN, breaker.state)
    }

    @Test fun `half-open lets exactly one trial through`() {
        fail(3)
        t = 1000
        assertTrue("the trial", breaker.allowRequest())
        assertFalse("a second caller must wait for the trial's result", breaker.allowRequest())
    }

    @Test fun `a successful trial closes the circuit`() {
        fail(3)
        t = 1000
        breaker.allowRequest()
        breaker.recordSuccess()
        assertEquals(State.CLOSED, breaker.state)
        fail(2)
        assertEquals("the count starts again from zero", State.CLOSED, breaker.state)
    }

    @Test fun `a failed trial reopens with a fresh timer`() {
        fail(3)
        t = 1000
        breaker.allowRequest()
        breaker.recordFailure()
        assertEquals("one failed trial is enough", State.OPEN, breaker.state)
        t = 1999
        assertFalse("the timer restarted at 1000", breaker.allowRequest())
        t = 2000
        assertTrue("1000 ms after reopening", breaker.allowRequest())
    }
}
