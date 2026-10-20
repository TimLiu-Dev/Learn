import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // StateFlow: always has a current value; collectors see the latest
    val state = MutableStateFlow(0)
    val ui = launch { state.collect { println("UI shows $it") } }
    delay(50)
    state.value = 1
    state.value = 2              // set twice without suspending: 1 is skipped
    delay(50)
    state.value = 2              // equal to the current value: no emission
    state.value = 3
    delay(50)
    println("state.value = ${state.value}")
    ui.cancel()

    // SharedFlow: no current value; every collector gets each event
    val events = MutableSharedFlow<String>()
    val a = launch { events.collect { println("A got $it") } }
    val b = launch { events.collect { println("B got $it") } }
    delay(50)
    events.emit("saved")
    events.emit("synced")
    delay(50)
    a.cancel(); b.cancel()
}
