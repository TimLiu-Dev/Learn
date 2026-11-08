fun discount(code: String): Int = when (code) {
    "HALF" -> 50
    "NONE" -> 0
    else -> TODO("discount for $code")
}

fun main() {
    val safe = runCatching { discount("VIP") }.getOrDefault(0)
    println("runCatching gave $safe")
    try {
        discount("VIP")
    } catch (e: Exception) {
        println("the Exception branch ran")
    } catch (e: Throwable) {
        println("the Throwable branch ran")
    }
}
