class Book(val title: String, val author: String = "Unknown", val pages: Int = 100) {
    // A secondary constructor whose parameters exactly match the call below
    constructor(title: String, author: String) : this(title, author, 1)
}

fun main() {
    val b = Book("Emma", "Austen")   // which constructor runs?
    println(b.pages)
}
