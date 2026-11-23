import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.thread
import kotlin.concurrent.write

// A phone book that is searched often and changed rarely.
class Directory(private val readLock: Lock, private val writeLock: Lock) {
    private val numbers = HashMap(mapOf("ana" to "555-0101", "bo" to "555-0102"))
    fun lookup(name: String): String? {
        readLock.lock()
        try { Thread.sleep(50); return numbers[name] }    // the sleep stands in for a slow search
        finally { readLock.unlock() }
    }
    fun update(name: String, number: String) {
        writeLock.lock()
        try { numbers[name] = number } finally { writeLock.unlock() }
    }
}

fun fourReaders(d: Directory): Long {
    val start = System.nanoTime()
    List(4) { thread { d.lookup("ana") } }.forEach { it.join() }
    return (System.nanoTime() - start) / 1_000_000
}

fun main() {
    val mutex = ReentrantLock()                              // one lock for both roles
    val rw = ReentrantReadWriteLock()
    val plain = Directory(mutex, mutex)
    val shared = Directory(rw.readLock(), rw.writeLock())
    repeat(2) {                                              // round 1 is warm-up
        println("4 lookups: ReentrantLock ${fourReaders(plain)} ms, read-write lock ${fourReaders(shared)} ms")
    }
    shared.update("bo", "555-0199")
    println("after update: bo = ${shared.lookup("bo")}")

    rw.read {                                                // Kotlin's extension: readLock + try/finally
        println("holding read lock, writeLock().tryLock() = ${rw.writeLock().tryLock()}")
    }
    rw.write {
        println("holding write lock, readLock().tryLock() = ${rw.readLock().tryLock()}")
        rw.readLock().unlock()
    }
}
