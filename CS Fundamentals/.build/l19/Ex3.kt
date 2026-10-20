data class Card(val rank: Int, val suit: Char)    // val + data class = a safe key

fun main() {
    val deck = HashSet<Card>()
    for (suit in "SHDC") for (rank in 1..13) deck.add(Card(rank, suit))
    println("cards: ${deck.size}")
    println("lookup with a new object: ${Card(12, 'H') in deck}")
    println("distinct hash codes: ${deck.map { it.hashCode() }.toSet().size}")
    println("Card(12, 'H').hashCode() = ${Card(12, 'H').hashCode()} = 31 * 12 + 'H'.code")
}
