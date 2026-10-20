import java.util.concurrent.Executors

fun main() {
    val pool = Executors.newFixedThreadPool(2)
    pool.submit { println("task ran on ${Thread.currentThread().name}") }
    println("main is done")
    // forgot pool.shutdown() / pool.close()
}
