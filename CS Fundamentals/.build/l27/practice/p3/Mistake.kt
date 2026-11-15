import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BoundedQueue<T>(private val capacity: Int) {
    private val items = ArrayDeque<T>()
    val lock = ReentrantLock()
    val notFull = lock.newCondition()
    val notEmpty = lock.newCondition()

    val size: Int get() = lock.withLock { items.size }

    // `if` instead of `while`: a woken thread doesn't re-check the condition.
    fun put(item: T): Unit = lock.withLock {
        if (items.size == capacity) notFull.await()
        items.addLast(item)
        notEmpty.signalAll()
    }

    fun take(): T = lock.withLock {
        if (items.isEmpty()) notEmpty.await()
        val item = items.removeFirst()
        notFull.signalAll()
        item
    }
}
