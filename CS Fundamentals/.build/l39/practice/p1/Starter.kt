import java.util.concurrent.CopyOnWriteArrayList

data class Change(val from: Int, val to: Int)

// Strategy: decides whether a change is worth announcing
typealias AlertRule = (from: Int, to: Int) -> Boolean

val onChange: AlertRule = { from, to -> TODO("announce only a real change") }

class PriceFeed(private var price: Int, private val rule: AlertRule = onChange) {
    private val listeners = CopyOnWriteArrayList<(Change) -> Unit>()

    fun subscribe(listener: (Change) -> Unit): AutoCloseable =
        TODO("add the listener; the handle removes this subscription and nothing else")

    fun update(newPrice: Int) {
        TODO("remember the new price; if the rule says so, tell every subscriber")
    }
}
