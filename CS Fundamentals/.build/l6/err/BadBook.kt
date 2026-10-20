class Book(val title: String, var pages: Int = 100) {
    init {
        require(pages > 0) { "pages must be positive" }
    }
}

fun main() {
    val bad = Book("Blank", 0)
}
