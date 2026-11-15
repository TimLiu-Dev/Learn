typealias FeePolicy = (daysLate: Int) -> Int

var rate = 25
val live: FeePolicy = { days -> days * rate }       // reads rate every time it runs
fun perDay(cents: Int): FeePolicy = { days -> days * cents }

fun main() {
    val fixed = perDay(rate)                        // the strategy is built now, with 25
    rate = 50
    println("${live(2)} ${fixed(2)}")
}
