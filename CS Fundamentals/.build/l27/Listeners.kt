// A listener that unregisters itself while the bus is notifying everyone.
import java.util.concurrent.CopyOnWriteArrayList

fun fire(listeners: MutableList<(String) -> Unit>) {
    lateinit var oneShot: (String) -> Unit
    oneShot = { e ->
        println("  one-shot got $e, unregistering")
        listeners.remove(oneShot)                    // modifies the list mid-iteration
    }
    listeners += oneShot
    listeners += { e -> println("  logger got $e") }
    for (l in listeners) l("saved")
    println("  listeners left: ${listeners.size}")
}

fun main() {
    println("CopyOnWriteArrayList:")
    fire(CopyOnWriteArrayList())
    println("ArrayList:")
    fire(mutableListOf())
}
