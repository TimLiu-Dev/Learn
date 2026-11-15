import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// A bounded blocking queue, like ArrayBlockingQueue: put waits while full, take waits while empty.
class BoundedQueue<T>(private val capacity: Int) {
    private val items = ArrayDeque<T>()
    val lock = ReentrantLock()
    val notFull = lock.newCondition()        // put() waits here
    val notEmpty = lock.newCondition()       // take() waits here

    val size: Int get() = lock.withLock { items.size }

    fun put(item: T): Unit = TODO("while full, wait on notFull; add; wake the takers")

    fun take(): T = TODO("while empty, wait on notEmpty; remove the first; wake the putters")
}
