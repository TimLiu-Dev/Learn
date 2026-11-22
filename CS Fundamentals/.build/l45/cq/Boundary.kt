import kotlin.random.Random

fun mayBorrow(openLoans: Int) = openLoans <= 3      // the rule: fewer than 3 open loans

fun main() {
    val rnd = Random(7)
    val failing = List(1_000) { rnd.nextInt(0, 10) }
        .filter { n -> mayBorrow(n) != (n < 3) }
        .toSortedSet()
    println(failing)
}
