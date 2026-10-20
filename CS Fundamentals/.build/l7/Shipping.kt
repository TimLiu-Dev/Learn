enum class Shipping(val days: Int) {
    STANDARD(5),
    EXPRESS(2),
    OVERNIGHT(1)
}

fun parseShipping(text: String): Shipping? =
    try {
        Shipping.valueOf(text.uppercase())
    } catch (e: IllegalArgumentException) {
        null
    }

fun main() {
    for (s in Shipping.entries) println("${s.name.lowercase()}: ${s.days} days")
    println(parseShipping("Express")?.days)
    println(parseShipping("teleport")?.days ?: "unknown method")
}
