// Four ways to create an expensive object on first use. 8 threads ask at the same moment.
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

val created = AtomicInteger()

class Expensive {
    init {
        created.incrementAndGet()
        Thread.sleep(20)                        // widen the race window
    }
}

// 1. Racy: check-then-act with no lock
var racy: Expensive? = null
fun racyGet(): Expensive = racy ?: Expensive().also { racy = it }

// 2. by lazy: SYNCHRONIZED by default, double-checked locking written for you
val lazyDefault by lazy { Expensive() }

// 3. by lazy(PUBLICATION): may run the initializer more than once, but everyone gets one value
val lazyPublication by lazy(LazyThreadSafetyMode.PUBLICATION) { Expensive() }

// 4. object: initialised once, when first used, by the JVM's class initialisation
object Registry {
    val instance = Expensive()
}

fun race(name: String, get: () -> Expensive) {
    created.set(0)
    val go = CountDownLatch(1)
    val seen = java.util.Collections.synchronizedSet(mutableSetOf<Expensive>())
    val threads = List(8) {
        thread {
            go.await()
            seen += get()
        }
    }
    go.countDown()                              // release all 8 at once
    threads.forEach { it.join() }
    println("$name: created ${created.get()} time(s), callers saw ${seen.size} instance(s)")
}

fun main() {
    race("racy            ") { racyGet() }
    race("by lazy         ") { lazyDefault }
    race("lazy PUBLICATION") { lazyPublication }
    race("object          ") { Registry.instance }
}
