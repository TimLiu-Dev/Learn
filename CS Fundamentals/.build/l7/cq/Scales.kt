import java.math.BigDecimal
import java.util.TreeSet

fun main() {
    val prices = listOf(BigDecimal("1.50"), BigDecimal("1.5"), BigDecimal("1.500"))
    println("${prices.toSet().size} ${TreeSet(prices).size} ${prices.distinct().size}")
}
