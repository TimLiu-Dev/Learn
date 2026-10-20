// Kotlin's @Deprecated carries a message, a suggested replacement, and a level.
@Deprecated(
    message = "Use formatPrice(cents) instead",
    replaceWith = ReplaceWith("formatPrice(cents)"),
    level = DeprecationLevel.WARNING,
)
fun oldPrice(cents: Int): String = "$" + cents / 100

fun formatPrice(cents: Int): String = "$%d.%02d".format(cents / 100, cents % 100)

@Suppress("DEPRECATION")            // silence the warning for one deliberate call
fun legacyReport(): String = oldPrice(250)

fun main() {
    println(oldPrice(1999))        // compiles, but kotlinc prints a warning
    println(formatPrice(1999))
    println(legacyReport())
}
