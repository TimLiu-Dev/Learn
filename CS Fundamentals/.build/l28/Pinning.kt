// run with: java -Djdk.virtualThreadScheduler.parallelism=1 -jar pinning.jar   (one carrier thread)
object SlowInit {                                 // its initializer blocks for 300 ms
    init { Thread.sleep(300) }
    fun touch() {}
}

fun time(label: String, jobs: List<() -> Unit>) {
    val start = System.nanoTime()
    val threads = jobs.map { job -> Thread.startVirtualThread(job) }
    threads.forEach { it.join() }
    println("%-40s %4d ms".format(label, (System.nanoTime() - start) / 1_000_000))
}

fun main() {
    println("java ${Runtime.version().feature()}")
    val locked = List(4) {
        val lock = Any()
        val job: () -> Unit = { synchronized(lock) { Thread.sleep(300) } }   // block holding a monitor
        job
    }
    time("4 x sleep(300) inside synchronized", locked)

    val mixed = listOf<() -> Unit>({ SlowInit.touch() }) +    // blocks inside <clinit>
        List(3) { { Thread.sleep(300) } }
    time("1 class init + 3 plain sleeps (300 each)", mixed)
}
