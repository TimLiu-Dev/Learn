import java.util.concurrent.atomic.LongAdder

fun main() {
    val done = LongAdder()                                   // many writers: L26's best counter
    val builder = Thread.ofVirtual().name("v-", 0)            // v-0, v-1, v-2, ...
    val start = System.nanoTime()
    val threads = List(100_000) {
        builder.start {
            Thread.sleep(100)
            done.increment()
        }
    }
    threads.forEach { it.join() }
    val ms = (System.nanoTime() - start) / 1_000_000
    val last = threads.last()
    println("${done.sum()} finished in $ms ms; last = ${last.name}, virtual = ${last.isVirtual}, daemon = ${last.isDaemon}")
}
