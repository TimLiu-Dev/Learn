package shop

// Everything the shop needs is injected (L38), so each pattern can be swapped or faked
class Shop(
    private val rules: List<PricingRule>,
    private val events: OrderEvents,
    private val notifierFor: (String) -> Notifier,
) {
    suspend fun place(order: Order): Long {
        val total = price(order, rules)
        events.publish(OrderEvent.Placed(order.id, total))
        notifierFor(order.notifyBy).send(order.customer, "order ${order.id} confirmed: ${cents(total)}")
        events.publish(OrderEvent.Notified(order.id, order.notifyBy.substringBefore(':')))
        return total
    }
}
