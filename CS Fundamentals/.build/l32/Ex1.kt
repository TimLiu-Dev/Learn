import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val payload = """
  {"orderId": "A-1001", "placedAt": "2026-10-20T14:05:00Z",
   "customer": {"name": "Ada", "email": "ada@example.com", "loyaltyTier": "gold"},
   "lines": [{"sku": "BK-1", "qty": 2, "priceCents": 1299},
             {"sku": "BK-7", "qty": 1, "priceCents": 2450}],
   "coupon": null, "giftWrap": true}
"""

@Serializable data class Customer(val name: String)                 // only what we need
@Serializable data class Line(val sku: String, val qty: Int, val priceCents: Long)
@Serializable data class Order(val orderId: String, val placedAt: Instant, val customer: Customer,
                               val lines: List<Line>, val coupon: String? = null)
@Serializable data class Summary(val orderId: String, val items: Int, val totalCents: Long)

val json = Json { ignoreUnknownKeys = true }     // payload has keys our DTOs don't

fun main() {
    val order = json.decodeFromString<Order>(payload)
    val items = order.lines.sumOf { it.qty }
    val total = order.lines.sumOf { it.qty * it.priceCents }
    println("${order.customer.name} ordered $items items on ${order.placedAt.toString().take(10)}")
    println("total: $%d.%02d".format(total / 100, total % 100))
    println(json.encodeToString(Summary(order.orderId, items, total)))
}
