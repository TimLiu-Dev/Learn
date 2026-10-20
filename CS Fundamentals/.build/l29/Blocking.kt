import kotlinx.coroutines.*

fun main() = runBlocking {
    val cores = Runtime.getRuntime().availableProcessors()
    for (fix in listOf(false, true)) {
        val t0 = System.nanoTime()
        val sleepers = List(cores) {                 // one per Default thread
            launch(Dispatchers.Default) {
                if (fix) withContext(Dispatchers.IO) { Thread.sleep(1000) }
                else Thread.sleep(1000)              // blocks a Default thread
            }
        }
        delay(50)
        launch(Dispatchers.Default) {                // a tiny, urgent job
            val ms = (System.nanoTime() - t0) / 1_000_000
            println(if (fix) "IO fix:   quick job ran at $ms ms"
                    else     "blocking: quick job ran at $ms ms")
        }.join()
        sleepers.joinAll()
    }
}
