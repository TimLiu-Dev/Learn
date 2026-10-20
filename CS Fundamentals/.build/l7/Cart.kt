enum class Tier(val percentOff: Int) { NONE(0), SILVER(5), GOLD(10) }

data class Cart(val items: Map<String, Int> = emptyMap(), val tier: Tier = Tier.NONE)

sealed interface Command
data class Add(val item: String, val qty: Int) : Command
data class Remove(val item: String) : Command
data class Upgrade(val tier: Tier) : Command
data object Clear : Command

fun apply(cart: Cart, command: Command): Cart = when (command) {
    is Add -> cart.copy(items = cart.items + (command.item to (cart.items[command.item] ?: 0) + command.qty))
    is Remove -> cart.copy(items = cart.items - command.item)
    is Upgrade -> cart.copy(tier = command.tier)
    Clear -> cart.copy(items = emptyMap())
}

fun main() {
    val commands = listOf(Add("apple", 2), Add("pear", 1), Add("apple", 3), Upgrade(Tier.GOLD), Remove("pear"), Clear)
    var cart = Cart()
    for (command in commands) {
        cart = apply(cart, command)
        println("$command -> $cart")
    }

    val oneStep = apply(Cart(), Add("apple", 5))
    val twoSteps = apply(apply(Cart(), Add("apple", 2)), Add("apple", 3))
    println(oneStep == twoSteps)

    val (items, tier) = apply(oneStep, Upgrade(Tier.SILVER))
    println("$items at ${tier.percentOff}% off")
}
