package shop

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val log = mutableListOf<String>()
    val events = OrderEvents()
    // The composition root wires the patterns together
    val shop = Shop(standardRules, events) { config -> LoggingNotifier(RetryingNotifier(notifierFrom(config), 3), log) }

    val audit = launch(start = CoroutineStart.UNDISPATCHED) { events.events.collect { println("  audit: $it") } }

    val orders = listOf(
        Order("A1", "ada@uni.test", listOf(Line("book", 2, 1250), Line("pen", 10, 99)), member = true),
        Order("B2", "+1-555-0100", listOf(Line("book", 1, 1250)), coupon = "SAVE5", notifyBy = "sms:2"),
    )
    for (order in orders) {
        println("placing ${order.id} (subtotal ${cents(order.subtotalCents)})")
        println("total ${cents(shop.place(order))}")
    }
    audit.cancel()
    println("notifier log: $log")
}
