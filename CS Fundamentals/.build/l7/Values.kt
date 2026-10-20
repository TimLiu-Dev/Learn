data class Money(val cents: Long, val currency: String) {
    init {
        require(cents >= 0) { "negative amount: $cents" }
    }
}

data class Tagged(val name: String) {
    var note: String = ""          // declared in the body: NOT part of equals/toString
}

fun main() {
    val price = Money(1250, "EUR")
    println(price)                                    // generated toString
    println("${price.cents} ${price.currency}")       // ordinary properties
    println(price == Money(1250, "EUR"))              // generated equals
    val raised = price.copy(cents = price.cents + 100) // copy, changing one property
    println(raised)

    val (cents, currency) = price                     // destructuring via component1(), component2()
    println("$cents in $currency")
    println(price.component2())

    try {
        Money(-5, "EUR")
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }

    val t1 = Tagged("x")
    t1.note = "first"
    val t2 = Tagged("x")
    t2.note = "second"
    println("${t1 == t2} $t1")
}
