package demo

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Counter {
    fun increment()
    val value: Int
}

class UnsafeCounter : Counter {                // step 1: the bug
    private var count = 0
    override fun increment() { count++ }
    override val value get() = count
}

class SyncCounter : Counter {                  // fix 1: the object's own monitor
    private var count = 0
    @Synchronized override fun increment() { count++ }
    override val value: Int @Synchronized get() = count
}

class LockCounter : Counter {                  // fix 2: an explicit lock
    private val lock = ReentrantLock()
    private var count = 0
    override fun increment() = lock.withLock { count++; Unit }
    override val value get() = lock.withLock { count }
}

class AtomicCounter : Counter {                // fix 3: lock-free compare-and-set
    private val count = AtomicInteger()
    override fun increment() { count.incrementAndGet() }
    override val value get() = count.get()
}
