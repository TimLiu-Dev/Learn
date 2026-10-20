import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

val created = AtomicInteger()               // counts constructor calls safely

class Config {
    init {
        created.incrementAndGet()
        Thread.sleep(5)                      // "expensive" setup widens the race window
    }
}

var instance: Config? = null
fun getConfig(): Config {
    if (instance == null) {                  // check...
        instance = Config()                  // ...then act: another thread may be here too
    }
    return instance!!
}

val lazyConfig by lazy { Config() }          // L15: synchronized by default

fun main() {
    repeat(5) { run ->
        created.set(0); instance = null
        hammer { getConfig() }
        println("run ${run + 1}: Config created ${created.get()} times by getConfig()")
    }
    created.set(0)
    hammer { lazyConfig }
    println("by lazy: Config created ${created.get()} time")
}

fun hammer(task: () -> Unit) {
    val gate = CountDownLatch(1)             // hold all 8 threads at the start line
    val threads = List(8) { thread { gate.await(); task() } }
    gate.countDown()                         // ...then release them together
    threads.forEach { it.join() }
}
