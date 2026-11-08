enum class Coin(val cents: Int) {
    PENNY(1), NICKEL(5), DIME(10), QUARTER(25)
}

fun total(coins: List<Coin>): Int = coins.sumOf { it.cents }

fun change(amount: Int): List<Coin> {
    val coins = mutableListOf<Coin>()
    var left = amount
    for (coin in Coin.entries) {
        while (left >= coin.cents) {
            coins += coin
            left -= coin.cents
        }
    }
    return coins
}
