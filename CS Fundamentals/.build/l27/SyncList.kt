// Every call on a synchronizedList is atomic. "Add if absent" is two calls, so it isn't.
import java.util.Collections
import kotlin.concurrent.thread

fun addAllIfAbsent(list: MutableList<Int>, clientLock: Boolean) {
    val threads = List(8) {
        thread {
            for (i in 0 until 10_000) {
                if (clientLock) {
                    synchronized(list) {                        // same lock the wrapper uses
                        if (i !in list) list.add(i)
                    }
                } else {
                    if (i !in list) list.add(i)                 // check, then act: a race
                }
            }
        }
    }
    threads.forEach { it.join() }
}

fun main() {
    val racy = Collections.synchronizedList(mutableListOf<Int>())
    addAllIfAbsent(racy, clientLock = false)
    println("no client lock:   size ${racy.size} (expected 10000)")

    val safe = Collections.synchronizedList(mutableListOf<Int>())
    addAllIfAbsent(safe, clientLock = true)
    println("with client lock: size ${safe.size} (expected 10000)")
}
