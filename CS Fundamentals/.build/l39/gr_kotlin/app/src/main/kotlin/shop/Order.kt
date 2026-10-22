package shop

data class Line(val sku: String, val qty: Int, val unitCents: Long)

// BUILDER, Kotlin style: named arguments + defaults replace the whole Builder class
data class Order(
    val id: String,
    val customer: String,
    val lines: List<Line>,
    val member: Boolean = false,
    val coupon: String? = null,
    val notifyBy: String = "email:orders@shop.test",
) {
    init {
        require(lines.isNotEmpty()) { "order $id has no lines" }
        require(lines.all { it.qty > 0 }) { "order $id has a non-positive quantity" }
    }
    val itemCount get() = lines.sumOf { it.qty }
    val subtotalCents get() = lines.sumOf { it.qty * it.unitCents }
}
