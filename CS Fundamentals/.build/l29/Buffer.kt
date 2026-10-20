import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

fun producer() = flow {
    for (i in 1..3) { delay(100); emit(i) }   // 100 ms to make each value
}

suspend fun timed(label: String, f: Flow<Int>) {
    val ms = measureTimeMillis {
        f.collect { delay(300); print("$it ") }  // 300 ms to use each value
    }
    println("  <- $label: $ms ms")
}

fun main() = runBlocking {
    timed("no buffer", producer())
    timed("buffer()", producer().buffer())
    timed("conflate()", producer().conflate())
}
