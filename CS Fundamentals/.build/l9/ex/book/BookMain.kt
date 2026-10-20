fun main() {
    val book = Book.sample()
    println(book.title)
    book.isAvailable = false
    println("available: ${book.isAvailable}")
    val subtitle: String? = book.subtitle
    println(subtitle ?: "(no subtitle)")
    println(subtitle?.length ?: 0)
}
