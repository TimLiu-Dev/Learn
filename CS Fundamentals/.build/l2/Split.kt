import java.math.BigDecimal
import java.math.RoundingMode

fun main() {
    val bill = BigDecimal("10.00")
    val three = BigDecimal("3")
    try {
        println(bill.divide(three))              // exact answer: 3.3333... forever
    } catch (e: ArithmeticException) {
        println("ArithmeticException: ${e.message}")
    }
    val share = bill.divide(three, 2, RoundingMode.HALF_EVEN)   // 2 decimal places
    println(share)
    println(share * three)                       // a cent has gone missing
    println(BigDecimal("10") / three)            // Kotlin's / keeps the scale of 10: zero decimals!

    // Fair split: work in whole cents, hand the leftover cents out one each
    val cents = bill.movePointRight(2).longValueExact()   // 1000
    val base = cents / 3
    val leftover = cents % 3
    var total = 0L
    for (i in 1..3) {
        val c = base + if (i <= leftover) 1 else 0   // the first `leftover` people pay a cent more
        total += c
        print("${BigDecimal.valueOf(c, 2)} ")        // c cents, shown with 2 decimals
    }
    println()
    println(BigDecimal.valueOf(total, 2))        // 10.00: nothing lost

    // Ties: HALF_EVEN rounds to the even neighbour, HALF_UP always up
    for (s in listOf("2.345", "2.355")) {
        val x = BigDecimal(s)
        println("$s -> ${x.setScale(2, RoundingMode.HALF_EVEN)} ${x.setScale(2, RoundingMode.HALF_UP)}")
    }
}
