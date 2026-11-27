import java.math.BigDecimal
import java.util.TreeSet

// Orders by price only, but equals (generated) compares name AND price
data class Product(val name: String, val cents: Long) : Comparable<Product> {
    override fun compareTo(other: Product): Int = cents.compareTo(other.cents)
}

// Consistent: compareTo returns 0 exactly when equals is true
data class Item(val name: String, val cents: Long) : Comparable<Item> {
    override fun compareTo(other: Item): Int {
        val byPrice = cents.compareTo(other.cents)
        return if (byPrice != 0) byPrice else name.compareTo(other.name)   // break ties
    }
}

fun main() {
    val a = BigDecimal("2.0")
    val b = BigDecimal("2.00")
    println("${a == b} ${a.compareTo(b)}")     // equals sees the scale, compareTo doesn't
    println("${hashSetOf(a, b).size} ${TreeSet(listOf(a, b)).size}")

    val tea = Product("tea", 300)
    val cake = Product("cake", 300)
    println("${hashSetOf(tea, cake).size} ${TreeSet(listOf(tea, cake)).size}")   // cake vanished
    println(TreeSet(listOf(Item("tea", 300), Item("cake", 300))))
}
