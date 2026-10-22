import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class LoanEvent(val kind: String, val title: String)

class LoanDesk {
    private val _events = MutableSharedFlow<LoanEvent>()         // hot: every collector gets every event
    val events: SharedFlow<LoanEvent> = _events.asSharedFlow()  // subscribers get a read-only view

    private val _onLoan = MutableStateFlow(0)                    // state: always has a current value
    val onLoan: StateFlow<Int> = _onLoan.asStateFlow()

    val subscribers get() = _events.subscriptionCount.value

    suspend fun lend(title: String) { _onLoan.update { it + 1 }; _events.emit(LoanEvent("lent", title)) }
    suspend fun giveBack(title: String) { _onLoan.update { it - 1 }; _events.emit(LoanEvent("returned", title)) }
}

fun main() = runBlocking {
    val desk = LoanDesk()
    // Subscribing = collecting; UNDISPATCHED makes each collector subscribe before we emit
    val screen = launch(start = CoroutineStart.UNDISPATCHED) {
        desk.events.collect { println("screen: ${it.title} ${it.kind}") }
    }
    val audit = launch(start = CoroutineStart.UNDISPATCHED) {
        desk.events.collect { println("audit:  ${it.kind}:${it.title}") }
    }
    desk.lend("Dune")
    desk.lend("Emma")
    desk.giveBack("Dune")
    println("on loan now: ${desk.onLoan.value}")
    println("subscribers while open: ${desk.subscribers}")
    screen.cancel()      // unsubscribing = cancelling the collector's coroutine
    audit.cancel()
    yield()              // let the cancellations finish
    println("subscribers after cancel: ${desk.subscribers}")
}
