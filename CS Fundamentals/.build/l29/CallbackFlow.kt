import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

// A listener API, shaped like Android's SensorManager or LocationManager.
fun interface Listener { fun onReading(celsius: Int) }

object Thermometer {
    val listeners = CopyOnWriteArrayList<Listener>()
    fun register(l: Listener) { listeners += l }
    fun unregister(l: Listener) { listeners -= l }
    init {                                            // a hardware thread pushes a reading every 50 ms
        thread(isDaemon = true, name = "sensor") {
            var t = 20
            while (true) { Thread.sleep(50); listeners.forEach { it.onReading(t++) } }
        }
    }
}

// The bridge: many callbacks become one cold Flow.
fun readings(): Flow<Int> = callbackFlow {
    val listener = Listener { trySend(it) }           // from the sensor thread into the flow's channel
    Thermometer.register(listener)
    awaitClose { Thermometer.unregister(listener) }   // runs when the collector stops
}

// The same without awaitClose.
fun leaky(): Flow<Int> = callbackFlow {
    Thermometer.register { trySend(it) }
}

fun main() = runBlocking {
    readings().take(3).collect { println("got $it °C on ${Thread.currentThread().name}") }
    println("listeners after take(3): ${Thermometer.listeners.size}")
    try { leaky().collect { } }
    catch (e: IllegalStateException) { println("leaky: ${e.message}") }
    println("listeners after leaky(): ${Thermometer.listeners.size}")
}
