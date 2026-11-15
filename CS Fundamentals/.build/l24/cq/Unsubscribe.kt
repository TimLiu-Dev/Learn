class Bus {
    private val listeners = mutableListOf<(String) -> Unit>()
    fun subscribe(l: (String) -> Unit) { listeners += l }
    fun unsubscribe(l: (String) -> Unit) { listeners -= l }
    val size get() = listeners.size
}

fun main() {
    val bus = Bus()
    bus.subscribe { e -> println(e) }
    bus.unsubscribe { e -> println(e) }    // the same code, written again
    println(bus.size)
}
