@Deprecated("Removed in 3.0: use formatPrice(cents)", level = DeprecationLevel.ERROR)
fun oldPrice(cents: Int): String = "$" + cents / 100

fun main() {
    println(oldPrice(1999))        // now a compile error, not a warning
}
