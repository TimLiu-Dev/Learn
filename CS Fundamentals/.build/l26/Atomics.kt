import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.LongAdder
import kotlin.concurrent.thread

val retries = LongAdder()                     // counts failed compare-and-set attempts

fun casIncrement(a: AtomicInteger) {
    while (true) {
        val old = a.get()                     // 1. read
        if (a.compareAndSet(old, old + 1)) return   // 2. write only if nobody changed it
        retries.increment()                   // 3. somebody did: loop and try again
    }
}

fun main() {
    val atomic = AtomicInteger()
    val manual = AtomicInteger()
    val adder = LongAdder()
    val workers = List(4) {
        thread {
            repeat(1_000_000) {
                atomic.incrementAndGet()
                casIncrement(manual)
                adder.increment()
            }
        }
    }
    workers.forEach { it.join() }
    println("incrementAndGet: ${atomic.get()}")
    println("my CAS loop:     ${manual.get()} after ${retries.sum()} retries")
    println("LongAdder:       ${adder.sum()}")
}
