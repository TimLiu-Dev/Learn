import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LockOrderTest {
    private fun locksTakenBy(action: () -> Unit): List<Int> {
        val ids = mutableListOf<Int>()
        onLock = { ids += it.id }
        try { action() } finally { onLock = {} }
        return ids
    }

    @Test fun `money moves, and an overdraft is refused`() {
        val a = Account(1, 100)
        val b = Account(2, 0)
        assertTrue(transfer(a, b, 60))
        assertFalse("only 40 left", transfer(a, b, 50))
        assertEquals(40L, a.balance)
        assertEquals(60L, b.balance)
    }

    @Test fun `the lower id is locked first, in both directions`() {
        val a = Account(1, 100)
        val b = Account(2, 100)
        assertEquals("locks taken by transfer(a, b)", listOf(1, 2), locksTakenBy { transfer(a, b, 10) })
        assertEquals("locks taken by transfer(b, a)", listOf(1, 2), locksTakenBy { transfer(b, a, 10) })
    }

    @Test fun `opposite transfers can't deadlock, even in the worst interleaving`() {
        val a = Account(1, 100)
        val b = Account(2, 100)
        lateinit var t1: Thread
        lateinit var t2: Thread
        val started = ConcurrentHashMap.newKeySet<Thread>()
        val holding = AtomicInteger()
        // After its FIRST lock, each thread waits until the other one holds a lock too,
        // or is blocked waiting for one. That forces the deadly interleaving if it's possible.
        onLock = {
            if (started.add(Thread.currentThread())) {
                holding.incrementAndGet()
                val other = if (Thread.currentThread() === t1) t2 else t1
                val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
                while (holding.get() < 2 && other.state != Thread.State.BLOCKED &&
                    System.nanoTime() < deadline) Thread.onSpinWait()
            }
        }
        t1 = thread(start = false, isDaemon = true) { transfer(a, b, 10) }
        t2 = thread(start = false, isDaemon = true) { transfer(b, a, 20) }
        t1.start(); t2.start()
        val end = System.currentTimeMillis() + 2000               // 2 s in all, not 2 s per thread
        for (t in listOf(t1, t2)) t.join(maxOf(1, end - System.currentTimeMillis()))
        onLock = {}
        assertFalse("deadlock: each thread holds one account and waits for the other", t1.isAlive || t2.isAlive)
        assertEquals("a", 110L, a.balance)
        assertEquals("b", 90L, b.balance)
    }

    @Test fun `audit locks every account in id order, all held at once`() {
        val accounts = listOf(Account(3, 30), Account(1, 10), Account(2, 20))
        val held = mutableListOf<Int>()
        var total = 0L
        val order = locksTakenBy {
            val record = onLock
            onLock = { record(it); held += accounts.count { a -> Thread.holdsLock(a) } }
            total = audit(accounts)
        }
        assertEquals("lock order", listOf(1, 2, 3), order)
        assertEquals("locks held as each lock is taken (each earlier lock must still be held)", listOf(1, 2, 3), held)
        assertEquals("total", 60L, total)
        assertEquals("an empty bank", 0L, audit(emptyList()))
    }
}
