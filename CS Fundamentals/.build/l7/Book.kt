data class Book(val title: String, val author: String, val year: Int) {
    init {
        require(title.isNotBlank()) { "title must not be blank" }
        require(year >= 1450) { "no printed books before 1450: $year" }
    }
}

fun main() {
    val draft = Book("Kotlin by Hand", "A. Learner", 2025)
    val fixed = draft.copy(year = 2026)
    println(draft)
    println(fixed)
    println(draft == fixed)
    val (title, _, year) = fixed           // _ skips a component you don't need
    println("$title ($year)")
    println(setOf(fixed, Book("Kotlin by Hand", "A. Learner", 2026)).size)
    try {
        Book(" ", "Nobody", 2000)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
}
