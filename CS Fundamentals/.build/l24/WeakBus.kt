import java.lang.ref.WeakReference

// The bus holds listeners weakly, so a forgotten unsubscribe no longer leaks.
object EventBus {
    private val listeners = mutableListOf<WeakReference<(String) -> Unit>>()
    fun subscribe(l: (String) -> Unit) { listeners += WeakReference(l) }
    fun publish(event: String): Int {                  // returns how many listeners got it
        listeners.removeAll { it.get() == null }       // drop cleared entries
        for (ref in listeners) ref.get()?.invoke(event)
        return listeners.size
    }
}

class Screen {
    val bitmap = ByteArray(1_000_000)
    private val listener: (String) -> Unit = { e -> onEvent(e) } // the Screen keeps its listener alive
    init { EventBus.subscribe(listener) }
    fun onEvent(e: String) { if (bitmap.isEmpty()) println(e) }
}

fun main() {
    val visible = Screen()                             // one screen the user is looking at
    for (i in 1..500) {
        Screen()                                       // opened and left: never unsubscribed
        if (i % 100 == 0) println("opened $i, delivered to ${EventBus.publish("tick")}")
    }
    System.gc()
    println("after GC, delivered to ${EventBus.publish("tick")}")
    println("visible screen still open: ${visible.bitmap.isNotEmpty()}")
}
