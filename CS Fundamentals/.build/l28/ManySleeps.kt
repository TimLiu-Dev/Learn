import java.lang.management.ManagementFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// usage: ManySleeps <virtual|pool200|platform> <tasks>
fun main(args: Array<String>) {
    val mode = args[0]
    val tasks = args[1].toInt()
    val executor: ExecutorService = when (mode) {
        "virtual" -> Executors.newVirtualThreadPerTaskExecutor()
        "pool200" -> Executors.newFixedThreadPool(200)
        "platform" -> Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory())
        else -> error("unknown mode $mode")
    }
    val start = System.nanoTime()
    executor.use { ex ->
        repeat(tasks) {
            ex.submit { Thread.sleep(1000) }                  // every task blocks for 1 s
        }
    }                                                         // close() waits for all of them
    val secs = (System.nanoTime() - start) / 1e9
    val peak = ManagementFactory.getThreadMXBean().peakThreadCount
    println("%-8s %,9d tasks: %6.2f s   peak platform threads: %d".format(mode, tasks, secs, peak))
}
