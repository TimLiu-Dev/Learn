import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates

data class LoanEvent(val kind: String, val title: String)

class LoanDesk {
    private val listeners = CopyOnWriteArrayList<(LoanEvent) -> Unit>()   // a listener is just a function type

    fun subscribe(listener: (LoanEvent) -> Unit): AutoCloseable {
        listeners += listener
        return AutoCloseable { listeners -= listener }
    }
    fun lend(title: String) = publish(LoanEvent("lent", title))
    fun giveBack(title: String) = publish(LoanEvent("returned", title))
    private fun publish(e: LoanEvent) = listeners.forEach { it(e) }
    val listenerCount get() = listeners.size
}

class FeeAccount {
    // A one-property observer, built in (L15)
    var feeCents: Int by Delegates.observable(0) { _, old, new -> println("fee $old -> $new") }
}

fun main() {
    val desk = LoanDesk()
    val audit = mutableListOf<String>()
    desk.subscribe { audit += "${it.kind}:${it.title}" }
    desk.subscribe { println("screen: ${it.title} ${it.kind}") }.use {
        desk.lend("Dune")
        desk.giveBack("Dune")
        println("listeners while screen open: ${desk.listenerCount}")
    }
    desk.lend("Emma")
    println("listeners after close: ${desk.listenerCount}")
    println("audit: $audit")

    val account = FeeAccount()
    account.feeCents = 250
    account.feeCents = 250   // observable fires on every assignment, equal or not
}
