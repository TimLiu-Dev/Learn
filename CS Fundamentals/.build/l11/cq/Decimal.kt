import java.util.Locale

fun price(cents: Long) = "%.2f".format(cents / 100.0)                    // uses the default locale
fun pricePinned(cents: Long) = "%.2f".format(Locale.ROOT, cents / 100.0)  // locale chosen explicitly

fun main() {
    Locale.setDefault(Locale.GERMANY)    // what a teammate's laptop in Berlin starts with
    println(price(1234) + " " + pricePinned(1234))
}
