import java.math.BigDecimal

fun main() {
    var total = 0.0
    for (i in 1..10) total += 0.1
    println(total)
    println(total == 1.0)

    var exact = BigDecimal.ZERO
    for (i in 1..10) exact += BigDecimal("0.1")   // Kotlin gives BigDecimal + and *
    println(exact)
    println(exact.compareTo(BigDecimal.ONE) == 0)

    println(BigDecimal(0.1))       // built from a Double: inherits its error
    val tolerance = 1e-9
    println(Math.abs(total - 1.0) < tolerance)
}
