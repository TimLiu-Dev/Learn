class Book(val title: String, val author: String = "Unknown", var pages: Int = 100) {
    init {
        require(pages > 0) { "pages must be positive" }
        println("created '$title'")
    }

    // Secondary constructor (a copy constructor): must delegate to the primary with this(...)
    constructor(other: Book) : this(other.title, other.author, other.pages)
}

fun main() {
    val a = Book("Dune", "Herbert", 412)
    val b = Book("Emma", "Austen")               // pages uses its default
    val c = Book(a)                              // secondary constructor: a separate copy
    val d = Book(title = "Ulysses", pages = 730) // named arguments, author defaulted
    c.pages = 500                                // changes the copy only
    println("${a.title} by ${a.author}, ${a.pages} pages")
    println("${b.title}: ${b.pages} pages")
    println("${c.title} (copy): ${c.pages} pages")
    println("${d.title} by ${d.author}, ${d.pages} pages")
}
