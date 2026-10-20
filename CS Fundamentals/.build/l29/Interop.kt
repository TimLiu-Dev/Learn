import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun legacyBlockingCall() = Thread.sleep(100)     // e.g. an old JDBC driver

fun main() = runBlocking {
    val virtual = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    for ((name, dispatcher) in listOf("Dispatchers.IO" to Dispatchers.IO,
                                      "virtual threads" to virtual)) {
        val ms = measureTimeMillis {
            coroutineScope {
                repeat(1000) { launch(dispatcher) { legacyBlockingCall() } }
            }
        }
        println("1000 blocking calls on %-16s %5d ms".format("$name:", ms))
    }
    virtual.close()
}
