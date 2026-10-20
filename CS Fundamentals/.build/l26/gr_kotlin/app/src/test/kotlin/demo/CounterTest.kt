package demo

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterTest {
    private fun hammer(counter: Counter, threads: Int = 8, perThread: Int = 100_000): Int {
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

    // One green run proves little, so each fix is hammered five times.
    private fun check(make: () -> Counter) = repeat(5) { assertEquals(800_000, hammer(make())) }

    @Test fun `synchronized counter never loses an update`() = check(::SyncCounter)
    @Test fun `ReentrantLock counter never loses an update`() = check(::LockCounter)
    @Test fun `AtomicInteger counter never loses an update`() = check(::AtomicCounter)
}
