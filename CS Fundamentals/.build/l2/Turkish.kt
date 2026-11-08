import java.util.Locale

fun main() {
    val turkish = Locale.forLanguageTag("tr-TR")
    println("title".uppercase(turkish))       // dotted capital İ
    println("TITLE".lowercase(turkish))       // dotless small ı

    Locale.setDefault(turkish)                // as on a laptop set up in Istanbul
    val command = "quit"
    println(command.uppercase() == "QUIT")    // uppercase() ignores the default locale
    println(command.uppercase(turkish) == "QUIT")
}
