package chat

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * The frames waiting to go to one subscriber, drained by that subscriber's own writer.
 * offer() never blocks: a full outbox means the subscriber is too slow, and the caller decides what to do.
 */
class Outbox(private val capacity: Int) {
    private val lock = ReentrantLock()
    private val ready = lock.newCondition()
    private val items = ArrayDeque<String>()
    private var closed = false

    /** false = full. After close() frames are dropped quietly and this returns true. */
    fun offer(item: String): Boolean = lock.withLock {
        if (closed) return true
        if (items.size >= capacity) return false
        items.addLast(item)
        ready.signal()
        true
    }

    /**
     * Ends the outbox: [last] (if any) becomes the final item, even past capacity.
     * [dropQueued] discards what's waiting first (for a slow consumer, whose backlog is the problem).
     * Returns false if it was already closed, so callers can run their cleanup exactly once.
     */
    fun close(last: String?, dropQueued: Boolean): Boolean = lock.withLock {
        if (closed) return false
        closed = true
        if (dropQueued) items.clear()
        if (last != null) items.addLast(last)
        ready.signalAll()
        true
    }

    /** Blocks until an item is ready. null = closed and fully drained. */
    fun take(): String? = lock.withLock {
        while (items.isEmpty() && !closed) ready.await()
        items.removeFirstOrNull()
    }

    fun isEmpty(): Boolean = lock.withLock { items.isEmpty() }

    val isClosed: Boolean get() = lock.withLock { closed }
}
