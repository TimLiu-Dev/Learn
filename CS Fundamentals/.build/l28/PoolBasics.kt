import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun main() {
    val start = System.nanoTime()
    Executors.newFixedThreadPool(3).use { pool ->          // close() waits for the tasks
        val futures = (1..6).map { id ->
            pool.submit(Callable {
                Thread.sleep(100)                           // pretend to do 100 ms of I/O
                "task $id ran on ${Thread.currentThread().name}"
            })
        }
        for (f in futures) println(f.get())                 // get() blocks until that task is done
    }
    val ms = (System.nanoTime() - start) / 1_000_000
    println("pool closed after $ms ms")
}
