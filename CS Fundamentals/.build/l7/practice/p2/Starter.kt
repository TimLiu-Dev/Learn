enum class Coin(val cents: Int) {
    PENNY(1), NICKEL(5), DIME(10), QUARTER(25)
}

fun total(coins: List<Coin>): Int = TODO("add up every coin's cents")

fun change(amount: Int): List<Coin> = TODO("fewest coins, largest first: take the biggest coin that still fits, again and again")
