import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    (1..10).asFlow()
        .onEach { println("  source $it") }
        .filter { it % 2 == 0 }
        .map { it * it }
        .take(3)
        .onCompletion { println("  completed") }
        .collect { println("got $it") }

    flowOf("a", "b")
        .transform { emit(it); emit(it.uppercase()) }   // 0..n values per input
        .collect { print("$it ") }
    println()

    flow { emit(1); throw IllegalStateException("sensor offline") }
        .catch { e -> println("caught: ${e.message}"); emit(-1) }
        .collect { println("value $it") }

    flow {
        println("emitting on   ${Thread.currentThread().name}")
        emit(42)
    }
        .flowOn(Dispatchers.IO)                          // changes UPSTREAM only
        .collect { println("collecting on ${Thread.currentThread().name}") }
}
