data class Tx(val id: Int, val customer: String, val category: String, val cents: Long, val refunded: Boolean)

val txs = listOf(
    Tx(1, "ada", "books", 2499, false), Tx(2, "lin", "games", 5999, false),
    Tx(3, "ada", "games", 1999, true), Tx(4, "sam", "books", 1250, false),
    Tx(5, "kim", "music", 899, false), Tx(6, "lin", "books", 3400, false),
    Tx(7, "sam", "games", 4999, false), Tx(8, "ada", "music", 1299, false),
    Tx(9, "kim", "books", 2100, true), Tx(10, "lin", "music", 999, false),
)

fun money(cents: Long) = "$%d.%02d".format(cents / 100, cents % 100)

fun <T, A> Iterable<T>.myFold(initial: A, combine: (A, T) -> A): A {
    var acc = initial
    for (item in this) acc = combine(acc, item)
    return acc
}

fun main() {
    val kept = txs.filterNot { it.refunded }

    println("revenue: " + money(kept.sumOf { it.cents }))

    println("by category:")
    kept.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.cents } }
        .entries.sortedByDescending { it.value }
        .forEach { (category, cents) -> println("  $category ${money(cents)}") }

    val top = kept.groupBy { it.customer }
        .mapValues { (_, list) -> list.sumOf { it.cents } }
        .maxByOrNull { it.value }
    println("top customer: ${top?.key} ${top?.value?.let(::money)}")

    println("refunded customers: " + txs.filter { it.refunded }.map { it.customer }.distinct().sorted())

    println("average by category:")
    kept.groupBy { it.category }.toSortedMap()
        .forEach { (category, list) -> println("  $category %.2f".format(list.map { it.cents }.average() / 100)) }

    val (count, cents) = txs.myFold(0 to 0L) { (n, sum), tx ->
        if (tx.refunded) (n + 1) to (sum + tx.cents) else n to sum
    }
    println("refunds: $count transactions, ${money(cents)}")
}
