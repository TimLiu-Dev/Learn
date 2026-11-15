import java.util.concurrent.CopyOnWriteArrayList

data class Change(val from: Int, val to: Int)

typealias AlertRule = (from: Int, to: Int) -> Boolean

val onChange: AlertRule = { from, to -> from != to }

class PriceFeed(private var price: Int, private val rule: AlertRule = onChange) {
    private val listeners = CopyOnWriteArrayList<(Change) -> Unit>()

    fun subscribe(listener: (Change) -> Unit): AutoCloseable {
        listeners += listener
        return AutoCloseable { listeners.removeIf { it === listener } }   // identity, but removes EVERY copy
    }

    fun update(newPrice: Int) {
        val change = Change(price, newPrice)
        price = newPrice
        if (rule(change.from, change.to)) listeners.forEach { it(change) }
    }
}
