import java.io.File

fun main() {
    val title = "Dune"
    println(title.take(10))                                   // clamps: no error case exists
    println(runCatching { title.substring(0, 10) }.exceptionOrNull())

    val shelf = listOf("Dune", "Emma")
    println(shelf.getOrNull(5))                               // null instead of a crash
    println(shelf.getOrElse(5) { "(empty slot)" })
    println(runCatching { shelf[5] }.exceptionOrNull())

    val openLoans = mutableMapOf(1L to "Ada")
    println(openLoans.remove(2L))                            // removing nothing is not an error

    val missing = File(System.getProperty("java.io.tmpdir"), "l37-does-not-exist.txt")
    println(missing.delete())                                 // false, no exception
}
