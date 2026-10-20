class Order {
    var id = 0
    var items = mutableListOf<String>()
    var express = false
    override fun toString() = "Order#$id $items express=$express"
}

fun parseQty(s: String): Int? = s.toIntOrNull()

fun main() {
    val order = Order()
    order.id = 7
    order.items.add("mat")
    order.items.add("band")
    order.express = true
    println("created $order")

    val qty = parseQty("3")
    if (qty != null) {
        println("qty doubled: ${qty * 2}")
    }

    val code = "  SAVE10 ".trim()
    val discount = if (code.startsWith("SAVE")) code else null
    println("discount: $discount")

    val total = listOf(12, 30, 8).sum()
    println("total before tax: $total")
    val withTax = total * 1.2
    println("with tax: $withTax")
}
