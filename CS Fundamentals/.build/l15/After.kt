class Order {
    var id = 0
    var items = mutableListOf<String>()
    var express = false
    override fun toString() = "Order#$id $items express=$express"
}

fun parseQty(s: String): Int? = s.toIntOrNull()

fun main() {
    val order = Order().apply {                       // configure: apply
        id = 7
        items += listOf("mat", "band")
        express = true
    }.also { println("created $it") }                 // side effect: also

    parseQty("3")?.let { println("qty doubled: ${it * 2}") }        // non-null: ?.let

    val discount = "  SAVE10 ".trim().takeIf { it.startsWith("SAVE") }   // keep or null: takeIf
    println("discount: $discount")

    val withTax = listOf(12, 30, 8).sum()
        .also { println("total before tax: $it") }
        .let { it * 1.2 }                              // transform: let
    println("with tax: $withTax")
}
