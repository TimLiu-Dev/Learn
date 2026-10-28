package shortener

import java.time.Clock
import java.time.LocalDate
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

/** The analytics queue (L42): redirects only enqueue; a background flusher batches the writes. */
class ClickRecorder(private val store: LinkStore, private val clock: Clock, capacity: Int = 100_000) {
    private val queue = LinkedBlockingQueue<String>(capacity)
    val dropped = AtomicLong()
    private val worker = Thread.ofVirtual().name("click-flusher").start {
        try {
            while (true) {
                Thread.sleep(200)
                flush()
            }
        } catch (e: InterruptedException) {
            // stop() asked us to finish
        }
    }

    /** Never blocks a redirect: if the queue is full, the click is dropped and counted. */
    fun record(code: String) {
        if (!queue.offer(code)) dropped.incrementAndGet()
    }

    /** Drain and write everything queued so far, in one transaction. One flush at a time. */
    @Synchronized fun flush() {
        val codes = ArrayList<String>()
        queue.drainTo(codes)
        if (codes.isNotEmpty()) store.addClicks(LocalDate.now(clock).toString(), codes.groupingBy { it }.eachCount())
    }

    fun stop() {
        worker.interrupt()
        worker.join()
        flush()
    }
}
