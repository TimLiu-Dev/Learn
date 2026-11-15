import kotlinx.coroutines.*

fun main() {
    val log = mutableListOf<String>()
    runBlocking {
        val job = launch {
            try {
                repeat(5) { i ->
                    log += "$i"
                    delay(100)
                }
            } catch (e: Exception) {
                log += "caught"
            }
            log += "after"
        }
        delay(250)
        job.cancelAndJoin()
        log += "done"
    }
    println(log.joinToString(" "))
}
