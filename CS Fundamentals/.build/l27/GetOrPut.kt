// 8 threads ask for the same missing key at once. How many times does the value get built?
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Connection                                    // pretend this is expensive to open

fun race(name: String, lookup: (ConcurrentHashMap<String, Connection>, () -> Connection) -> Connection) {
    val map = ConcurrentHashMap<String, Connection>()
    val built = AtomicInteger()
    val go = CountDownLatch(1)
    val seen = ConcurrentHashMap.newKeySet<Connection>()
    val threads = List(8) {
        thread {
            go.await()
            seen += lookup(map) { built.incrementAndGet(); Thread.sleep(20); Connection() }
        }
    }
    go.countDown()
    threads.forEach { it.join() }
    println("$name built ${built.get()}, map holds ${map.size}, callers saw ${seen.size}")
}

fun main() {
    race("getOrPut        ") { m, make -> m.getOrPut("db") { make() } }
    race("computeIfAbsent ") { m, make -> m.computeIfAbsent("db") { make() } }
}
