class Strict<T : Any>(val item: T)

fun main() {
    val bad = Strict<String?>(null)
}
