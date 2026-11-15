import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class RunAllTest {
    @Test fun `results come back in task order`() {
        assertEquals(listOf("a", "b", "c"), runAll("w", listOf({ "a" }, { "b" }, { "c" })))
    }

    @Test fun `each task runs on its own named thread`() {
        val myName = { Thread.currentThread().name }
        assertEquals(listOf("job-0", "job-1", "job-2"), runAll("job", listOf(myName, myName, myName)))
    }

    @Test fun `the tasks really run at the same time`() {
        val arrived = CountDownLatch(3)
        val meet = {
            arrived.countDown()                                   // "I'm here"
            if (arrived.await(2, TimeUnit.SECONDS)) "met" else "waited alone for 2 s"
        }
        assertEquals("each task waits until all three are running",
            listOf("met", "met", "met"), runAll("m", listOf(meet, meet, meet)))
    }

    @Test fun `every thread has finished when runAll returns`() {
        val seen = Collections.synchronizedList(mutableListOf<Thread>())
        val slow = { seen.add(Thread.currentThread()); Thread.sleep(100); "done" }
        assertEquals(listOf("done", "done"), runAll("s", listOf(slow, slow)))
        assertEquals("thread states after runAll",
            listOf(Thread.State.TERMINATED, Thread.State.TERMINATED), seen.map { it.state })
    }

    @Test fun `no tasks means an empty list`() {
        assertEquals(emptyList<String>(), runAll("x", emptyList()))
    }
}
