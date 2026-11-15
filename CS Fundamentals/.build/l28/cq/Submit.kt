import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val pool = Executors.newFixedThreadPool(2)
    try {
        val answer = pool.submit { 6 * 7 }
        println("answer = ${answer.get()}")
    } finally {
        pool.shutdown()
        pool.awaitTermination(5, TimeUnit.SECONDS)
    }
}
