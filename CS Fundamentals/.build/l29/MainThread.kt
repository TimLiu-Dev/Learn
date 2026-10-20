import kotlinx.coroutines.*
import java.util.concurrent.Executors

// A pretend UI thread. A real app gets Dispatchers.Main from its UI library.
val Main = Executors.newSingleThreadExecutor { r -> Thread(r, "ui") }
    .asCoroutineDispatcher()
var t0 = 0L
fun log(msg: String) =
    println("%5d ms [%s] %s".format((System.nanoTime() - t0) / 1_000_000,
        Thread.currentThread().name.substringBefore(" @"), msg))

fun loadReport(): String { Thread.sleep(400); return "42 rows" }   // blocking I/O

suspend fun screen(blockTheUi: Boolean) = coroutineScope {
    t0 = System.nanoTime()
    val frames = launch(Main) {                    // the UI keeps drawing
        repeat(6) { log("frame $it"); delay(100) }
    }
    launch(Main) {                                 // a button click
        delay(150)
        log("click")
        val report = if (blockTheUi) loadReport()  // BAD: on the UI thread
                     else withContext(Dispatchers.IO) { loadReport() }
        log("show $report")                        // back on the UI thread
    }
    frames.join()
}

fun main() = runBlocking {
    println("== blocking the UI thread"); screen(blockTheUi = true)
    println("== withContext(Dispatchers.IO)"); screen(blockTheUi = false)
    Main.close()
}
