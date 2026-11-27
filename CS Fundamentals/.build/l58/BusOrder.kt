// Two in-process event buses. Both deliver synchronously, on the publisher's thread.
// They differ only in what happens when a handler publishes another event.
interface Bus {
    fun subscribe(handler: (String) -> Unit)
    fun publish(event: String)
}

class ImmediateBus : Bus {
    private val handlers = mutableListOf<(String) -> Unit>()
    override fun subscribe(handler: (String) -> Unit) { handlers += handler }
    override fun publish(event: String) = handlers.forEach { it(event) }   // nested publish runs right now
}

class QueuedBus : Bus {
    private val handlers = mutableListOf<(String) -> Unit>()
    private val queue = ArrayDeque<String>()
    private var delivering = false
    override fun subscribe(handler: (String) -> Unit) { handlers += handler }
    override fun publish(event: String) {
        queue.addLast(event)
        if (delivering) return                       // finish the current event first
        delivering = true
        try {
            while (queue.isNotEmpty()) { val e = queue.removeFirst(); handlers.forEach { it(e) } }
        } finally { delivering = false }
    }
}

fun trace(bus: Bus): List<String> {
    val log = mutableListOf<String>()
    // Lending: put a new copy on the shelf, then announce that it's ready to lend.
    bus.subscribe { e -> if (e == "CopyAdded") { log += "lending saw $e"; bus.publish("CopyLendable") } }
    // Notifications: tell members on the waiting list. It must hear about the copy before it's lendable.
    bus.subscribe { e -> log += "notifications saw $e" }
    bus.publish("CopyAdded")
    return log
}

fun main() {
    println("immediate: " + trace(ImmediateBus()))
    println("queued:    " + trace(QueuedBus()))
}
