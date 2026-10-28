package gateway

import java.time.Duration
import java.time.Instant
import java.time.InstantSource
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CircuitBreakerTest {
    private val now = AtomicLong(0)
    private val clock = InstantSource { Instant.ofEpochMilli(now.get()) }      // fake clock: we move time by hand
    private val breaker = CircuitBreaker(5, Duration.ofSeconds(1), clock)
    private fun fail() = assertFailsWith<IllegalStateException> { breaker.call { error("boom") } }

    @Test fun `opens after five consecutive failures`() {
        repeat(4) { fail() }
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state())
        fail()
        assertEquals(CircuitBreaker.State.OPEN, breaker.state())
    }

    @Test fun `a success resets the failure count`() {
        repeat(4) { fail() }
        breaker.call { "ok" }
        repeat(4) { fail() }
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state())
    }

    @Test fun `while open it fails fast without calling`() {
        repeat(5) { fail() }
        var called = false
        assertFailsWith<CircuitOpenException> { breaker.call { called = true } }
        assertEquals(false, called)
    }

    @Test fun `after the open period one trial call can close it`() {
        repeat(5) { fail() }
        now.addAndGet(1000)
        assertEquals(CircuitBreaker.State.HALF_OPEN, breaker.state())
        assertEquals("ok", breaker.call { "ok" })
        assertEquals(CircuitBreaker.State.CLOSED, breaker.state())
    }

    @Test fun `a failed trial call opens it again`() {
        repeat(5) { fail() }
        now.addAndGet(1000)
        fail()
        assertEquals(CircuitBreaker.State.OPEN, breaker.state())
        now.addAndGet(999)
        assertEquals(CircuitBreaker.State.OPEN, breaker.state())
    }
}
