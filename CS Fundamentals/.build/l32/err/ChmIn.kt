import java.util.concurrent.ConcurrentHashMap

fun main() {
    val books = ConcurrentHashMap<Long, String>()
    books[1L] = "Dune"
    println(1L in books)
}
