import kotlinx.coroutines.*

fun main() {
    val log = mutableListOf<String>()
    runBlocking {
        launch { delay(200); log += "A" }
        launch { delay(100); log += "B" }
        log += "C"
        coroutineScope {
            launch { delay(50); log += "D" }
        }
        log += "E"
    }
    println(log.joinToString(" "))
}
