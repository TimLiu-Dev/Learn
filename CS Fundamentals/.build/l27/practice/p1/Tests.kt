import java.lang.reflect.Modifier
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OnceTest {
    @Test fun `one caller gets the value, and init runs once`() {
        val calls = AtomicInteger()
        val once = Once { calls.incrementAndGet(); "config" }
        assertEquals("config", once.get())
        assertEquals("config", once.get())
        assertEquals("init calls", 1, calls.get())
    }

    @Test fun `a second caller waits instead of running init again`() {
        val calls = AtomicInteger()
        val insideInit = CountDownLatch(1)
        val release = CountDownLatch(1)
        val once = Once {
            calls.incrementAndGet()
            insideInit.countDown()
            release.await(5, TimeUnit.SECONDS)        // init stays busy until the test says so
            Any()
        }
        val got = arrayOfNulls<Any>(2)
        val a = thread(isDaemon = true) { got[0] = once.get() }
        insideInit.await(5, TimeUnit.SECONDS)         // a is now inside init
        val b = thread(isDaemon = true) { got[1] = once.get() }
        // Wait until b is stuck behind the lock, or has started init itself.
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (b.state != Thread.State.BLOCKED && b.state != Thread.State.WAITING &&
            calls.get() < 2 && System.nanoTime() < deadline) Thread.onSpinWait()
        release.countDown()
        a.join(5000); b.join(5000)
        assertEquals("init calls", 1, calls.get())
        assertSame("both callers must get the same object", got[0], got[1])
    }

    @Test fun `once the value exists, get() doesn't touch the lock`() {
        val once = Once { "ready" }
        once.get()
        var got: String? = null
        val reader: Thread
        synchronized(once.lock) {                     // another thread is busy holding the lock
            reader = thread(isDaemon = true) { got = once.get() }
            reader.join(2000)
        }
        assertFalse("get() waited for the lock although the value was already set", reader.isAlive)
        assertEquals("ready", got)
    }

    @Test fun `the field is volatile, so the fast path never sees a half-built object`() {
        val field = Once::class.java.getDeclaredField("value")
        assertTrue("value is read without the lock, so it needs @Volatile", Modifier.isVolatile(field.modifiers))
    }
}
