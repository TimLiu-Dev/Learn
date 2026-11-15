import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EmailWorkerTest {
    private val sent = mutableListOf<String>()
    private val worker = EmailWorker { sent += it }

    @Test fun `each new message sends its email`() {
        worker.handle(Message(1, "welcome ada", 1))
        worker.handle(Message(2, "welcome bob", 1))
        assertEquals(listOf("welcome ada", "welcome bob"), sent)
    }

    @Test fun `a redelivered message sends nothing the second time`() {
        worker.handle(Message(1, "welcome ada", 1))
        worker.handle(Message(1, "welcome ada", 2))
        assertEquals(listOf("welcome ada"), sent)
        assertEquals("deliveries skipped", 1, worker.skipped)
    }

    @Test fun `two jobs with the same text are still two jobs`() {
        worker.handle(Message(7, "your book is due", 1))
        worker.handle(Message(8, "your book is due", 1))
        assertEquals("ids 7 and 8 are different jobs", 2, sent.size)
    }

    @Test fun `a send that fails is retried on redelivery`() {
        var down = true                                // the mail server is down for the first try
        val flaky = EmailWorker { if (down) { down = false; error("mail server down") } else sent += it }
        assertThrows("the failure must reach the queue, so it doesn't ack", IllegalStateException::class.java) {
            flaky.handle(Message(3, "welcome cy", 1))
        }
        flaky.handle(Message(3, "welcome cy", 2))
        assertEquals("the second attempt sends it", listOf("welcome cy"), sent)
    }

    @Test fun `an at-least-once run sends every email exactly once`() {
        val delivered = listOf(1, 2, 3, 2, 4, 5, 6, 2, 1)   // the queue redelivered #2 twice and #1 once
        val attempts = mutableMapOf<Int, Int>()
        for (id in delivered) {
            val attempt = attempts.merge(id, 1, Int::plus)!!
            worker.handle(Message(id.toLong(), "job $id", attempt))
        }
        assertEquals((1..6).map { "job $it" }, sent)
        assertEquals("deliveries skipped", 3, worker.skipped)
    }
}
