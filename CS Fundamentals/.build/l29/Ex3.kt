import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// A reading every 100 ms; -999 is a glitch; then the sensor is unplugged
fun sensor(): Flow<Int> = flow {
    for (c in listOf(21, 22, -999, 23, 24)) { delay(100); emit(c) }
    delay(100)
    throw IllegalStateException("sensor unplugged")
}

val latest = MutableStateFlow<Double?>(null)      // what a UI would observe

suspend fun watch(limit: Int?) {
    sensor()
        .filter { it != -999 }
        .map { c -> c * 9.0 / 5 + 32 }
        .onEach { latest.value = it }
        .let { if (limit != null) it.take(limit) else it }
        .catch { e -> println("  sensor error: ${e.message}") }
        .onCompletion { println("  completed") }
        .collect { println("  %.1f °F".format(it)) }
}

fun main() = runBlocking {
    println("take(3):"); watch(3)
    println("no limit:"); watch(null)
    println("latest = ${latest.value}")
}
