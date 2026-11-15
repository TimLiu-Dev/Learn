import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// A bounded blocking queue, like ArrayBlockingQueue: put waits while full, take waits while empty.
class BoundedQueue<T>(private val capacity: Int) {
    private val items = ArrayDeque<T>()
    val lock = ReentrantLock()
    val notFull = lock.newCondition()        // put() waits here
    val notEmpty = lock.newCondition()       // take() waits here

    val size: Int get() = lock.withLock { items.size }

    fun put(item: T): Unit = lock.withLock {
        while (items.size == capacity) notFull.await()   // a loop: re-check after every wake-up
        items.addLast(item)
        notEmpty.signalAll()
    }

    fun take(): T = lock.withLock {
        while (items.isEmpty()) notEmpty.await()
        val item = items.removeFirst()
        notFull.signalAll()
        item
    }
}
