import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GateTest {
    @Test fun `enter returns the block's value and gives the permit back`() {
        val gate = Gate(2)
        assertEquals(42, gate.enter { 42 })
        assertEquals("permits after enter", 2, gate.available())
    }

    @Test fun `while a block runs, its permit is taken`() {
        val gate = Gate(2)
        assertEquals("permits seen from inside the block", 1, gate.enter { gate.available() })
    }

    @Test fun `a failing block still gives its permit back`() {
        val gate = Gate(1)
        val e = assertThrows(IllegalStateException::class.java) { gate.enter { error("query failed") } }
        assertEquals("the block's own exception", "query failed", e.message)
        assertEquals("permits after a failure", 1, gate.available())
    }

    @Test fun `20 callers on 8 threads, never more than 3 inside`() {
        val gate = Gate(3)
        val inside = AtomicInteger()
        val maxSeen = AtomicInteger()
        val pool = Executors.newFixedThreadPool(8)
        try {
            val futures = (1..20).map { id ->
                pool.submit(Callable {
                    gate.enter {
                        maxSeen.accumulateAndGet(inside.incrementAndGet()) { a, b -> maxOf(a, b) }
                        Thread.sleep(5)             // stay inside a moment, so callers overlap
                        inside.decrementAndGet()
                        id
                    }
                })
            }
            assertEquals("sum of every caller's id", 210, futures.sumOf { it.get() })
            assertTrue("at most 3 inside at once, saw ${maxSeen.get()}", maxSeen.get() <= 3)
            assertEquals("permits at the end", 3, gate.available())
        } finally {
            pool.shutdown()
            pool.awaitTermination(5, TimeUnit.SECONDS)
        }
    }
}
