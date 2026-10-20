// A long-lived event bus keeps every subscriber reachable until it unsubscribes.
object EventBus {                                    // lives as long as the app
    private val listeners = mutableListOf<(String) -> Unit>()
    fun subscribe(l: (String) -> Unit) { listeners += l }
    fun unsubscribe(l: (String) -> Unit) { listeners -= l }
    val size get() = listeners.size
}

var fixed = false

class Screen {
    private val bitmap = ByteArray(1_000_000)               // 1 MB of pixels
    private val listener: (String) -> Unit = { e -> onEvent(e) } // captures this
    init { EventBus.subscribe(listener) }
    fun onEvent(e: String) = println("$e ${bitmap.size}")
    fun close() {
        if (fixed) EventBus.unsubscribe(listener)    // the line people forget
    }
}

fun main(args: Array<String>) {
    fixed = args.isNotEmpty()
    for (i in 1..500) {
        val s = Screen()
        s.close()                                    // the user left the screen
        if (i % 20 == 0) println("opened $i, listeners ${EventBus.size}")
    }
}
