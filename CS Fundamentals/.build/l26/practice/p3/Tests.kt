import java.lang.management.ManagementFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import org.junit.Assert.assertEquals
import org.junit.Test

// True once thread t is stuck at a lock that the calling thread holds.
@Suppress("DEPRECATION")                         // Thread.id: the Playground's Java 17 has no threadId()
fun waitsForMe(t: Thread): Boolean =
    ManagementFactory.getThreadMXBean().getThreadInfo(t.id)?.lockOwnerId == Thread.currentThread().id

class MemoTest {
    @Test fun `one thread loads each key once`() {
        val memo = Memo { it.uppercase() }
        assertEquals("A", memo.get("a"))
        assertEquals("A", memo.get("a"))
        assertEquals("B", memo.get("b"))
        assertEquals("loads", 2, memo.loads)
    }

    @Test fun `a thread that asks mid-load waits instead of loading again`() {
        val calls = AtomicInteger()
        lateinit var memo: Memo
        lateinit var second: Thread
        memo = Memo { key ->
            if (calls.incrementAndGet() == 1) {
                // First load: start a second thread that wants the same key, and wait
                // until it either loads the key too (the race) or stops at a lock.
                second = thread { memo.get(key) }
                while (calls.get() == 1 && second.isAlive && !waitsForMe(second)) Thread.onSpinWait()
            }
            "value of $key"
        }
        assertEquals("value of k", memo.get("k"))
        second.join()
        assertEquals("loader calls for one key (the second thread loaded it again)", 1, calls.get())
        assertEquals("loads", 1, memo.loads)
    }

    @Test fun `8 threads, 200 keys, every key loaded exactly once`() {
        repeat(5) { round ->
            val calls = AtomicInteger()
            val memo = Memo { calls.incrementAndGet(); "v$it" }
            val gate = CountDownLatch(1)                  // hold all threads at the start line
            val wrong = AtomicInteger()
            val threads = List(8) { t ->
                thread {
                    gate.await()
                    for (i in 0 until 200) {
                        val key = "k${(i + t * 25) % 200}"  // each thread starts at a different key
                        if (memo.get(key) != "v$key") wrong.incrementAndGet()
                    }
                }
            }
            gate.countDown()
            threads.forEach { it.join() }
            assertEquals("round $round: loader calls", 200, calls.get())
            assertEquals("round $round: loads", 200, memo.loads)
            assertEquals("round $round: wrong values returned", 0, wrong.get())
        }
    }
}
