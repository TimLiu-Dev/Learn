import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

data class Message(val id: Long, val body: String, val attempts: Int = 0)

// An at-least-once queue: a message stays "in flight" until acked, and comes back if it isn't.
class JobQueue(capacity: Int, private val visibilityMs: Long, private val maxAttempts: Int) : AutoCloseable {
    private val ready = LinkedBlockingQueue<Message>(capacity)
    private val inFlight = ConcurrentHashMap<Long, Pair<Message, Long>>()   // id -> (message, deadline)
    val deadLetters = CopyOnWriteArrayList<Message>()
    private val ids = AtomicLong()
    @Volatile private var running = true

    init { Thread.ofVirtual().start { sweep() } }

    fun push(body: String) = ready.put(Message(ids.incrementAndGet(), body))           // blocks while full
    fun offer(body: String): Boolean = ready.offer(Message(ids.incrementAndGet(), body)) // false while full

    fun receive(timeoutMs: Long): Message? {
        val m = ready.poll(timeoutMs, TimeUnit.MILLISECONDS) ?: return null
        val delivery = m.copy(attempts = m.attempts + 1)
        inFlight[m.id] = delivery to System.currentTimeMillis() + visibilityMs
        return delivery
    }

    fun ack(id: Long): Boolean = inFlight.remove(id) != null

    // Every 20 ms: anything in flight past its deadline goes back to the queue, or to the dead letters.
    private fun sweep() {
        while (running) {
            val now = System.currentTimeMillis()
            for ((id, entry) in inFlight) {
                if (entry.second <= now && inFlight.remove(id, entry)) {
                    val m = entry.first
                    if (m.attempts >= maxAttempts) deadLetters += m else ready.put(m)
                }
            }
            Thread.sleep(20)
        }
    }

    override fun close() { running = false }
}
