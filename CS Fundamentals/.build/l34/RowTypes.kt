// One row of the books table as a Kotlin type.
// NOT NULL columns become non-null types; the nullable year becomes Int?.
data class Book(
    val id: Int,
    val title: String,      // title TEXT NOT NULL
    val authorId: Int?,     // author_id has no NOT NULL
    val year: Int?,         // year can be NULL (Beowulf)
    val priceCents: Int,    // price_cents INTEGER NOT NULL
)

fun main() {
    val beowulf = Book(19, "Beowulf", 7, null, 799)
    println(beowulf)
    println("age: ${beowulf.year?.let { 2026 - it } ?: "unknown"}")
}
