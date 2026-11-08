enum class Coin(val cents: Int) {
    PENNY(1), NICKEL(5), DIME(10), QUARTER(25)
}

fun total(coins: List<Coin>): Int = coins.sumOf { it.cents }

fun change(amount: Int): List<Coin> {
    val coins = mutableListOf<Coin>()
    var left = amount
    // Sort by the data, so reordering the constants can't break this.
    for (coin in Coin.entries.sortedByDescending { it.cents }) {
        while (left >= coin.cents) {
            coins += coin
            left -= coin.cents
        }
    }
    return coins
}
