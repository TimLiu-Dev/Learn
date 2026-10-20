import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val start = System.nanoTime()
    fun stamp() = "%4d ms".format((System.nanoTime() - start) / 1_000_000)

    // a ThreadFactory that names its threads: "ticker-0", "ticker-1", ...
    val factory = Thread.ofPlatform().name("ticker-", 0).factory()
    val scheduler = Executors.newScheduledThreadPool(1, factory)

    scheduler.schedule({ println("${stamp()} one-off reminder on ${Thread.currentThread().name}") },
        250, TimeUnit.MILLISECONDS)
    val ticks = scheduler.scheduleAtFixedRate({ println("${stamp()} tick") },
        0, 100, TimeUnit.MILLISECONDS)

    Thread.sleep(450)
    ticks.cancel(false)                     // stop the repeating task
    scheduler.shutdown()
    println("${stamp()} terminated: ${scheduler.awaitTermination(1, TimeUnit.SECONDS)}")
}
