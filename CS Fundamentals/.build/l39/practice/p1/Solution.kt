import java.util.concurrent.CopyOnWriteArrayList

data class Change(val from: Int, val to: Int)

// Strategy: decides whether a change is worth announcing
typealias AlertRule = (from: Int, to: Int) -> Boolean

val onChange: AlertRule = { from, to -> from != to }

class PriceFeed(private var price: Int, private val rule: AlertRule = onChange) {
    // One object per subscription. It isn't a data class, so equality is identity,
    // and remove() can only ever find this exact subscription.
    private class Subscription(val listener: (Change) -> Unit)

    private val subs = CopyOnWriteArrayList<Subscription>()

    fun subscribe(listener: (Change) -> Unit): AutoCloseable {
        val sub = Subscription(listener)
        subs += sub
        return AutoCloseable { subs -= sub }       // a second close finds nothing to remove
    }

    fun update(newPrice: Int) {
        val change = Change(price, newPrice)
        price = newPrice
        if (rule(change.from, change.to)) subs.forEach { it.listener(change) }
    }
}
