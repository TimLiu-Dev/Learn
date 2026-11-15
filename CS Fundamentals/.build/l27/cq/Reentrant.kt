import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

fun main() {
    val lock = ReentrantLock()
    lock.lock()
    lock.lock()
    val holds = lock.holdCount
    lock.unlock()

    var other = false
    thread { other = lock.tryLock() }.join()
    val first = other

    lock.unlock()
    thread { other = lock.tryLock() }.join()    // this thread never unlocks
    println("$holds $first $other ${lock.isLocked}")
}
