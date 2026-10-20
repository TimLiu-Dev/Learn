package demo

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterTest {
    private fun hammer(counter: Counter, threads: Int, perThread: Int): Int {
        val ready = CountDownLatch(threads)     // every worker has started...
        val go = CountDownLatch(1)              // ...then one start signal for all
        val workers = List(threads) {
            thread {
                ready.countDown()
                go.await()
                repeat(perThread) { counter.increment() }
            }
        }
        ready.await()
        go.countDown()
        workers.forEach { it.join() }
        return counter.value
    }

    @Test
    fun `8 threads x 100_000 increments give 800_000`() {
        assertEquals(800_000, hammer(UnsafeCounter(), threads = 8, perThread = 100_000))
    }
}
