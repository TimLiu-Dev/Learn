import kotlin.concurrent.thread

class SyncCounter {
    private var count = 0
    @Synchronized fun increment() { count++ }          // lock = this object
    @Synchronized fun addTwice() { increment(); increment() }   // re-enters the lock it holds
    @Synchronized fun get() = count
}

class BlockCounter {
    private val lock = Any()                           // a private lock nobody else can grab
    private var count = 0
    fun increment() { synchronized(lock) { count++ } }
    fun get() = synchronized(lock) { count }
}

fun main() {
    repeat(3) { run ->
        val s = SyncCounter(); val b = BlockCounter()
        val workers = List(2) { thread { repeat(1_000_000) { s.increment(); b.increment() } } }
        workers.forEach { it.join() }
        println("run ${run + 1}: @Synchronized = ${s.get()}, synchronized(lock) = ${b.get()}")
    }
    val s = SyncCounter()
    s.addTwice()
    println("reentrant: addTwice() -> ${s.get()}")
}
