import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class Book(val id: Long, val title: String, val year: Int)
@Serializable data class NewBook(val title: String, val year: Int)

@Serializable                       // one envelope for every list endpoint
data class Page<T>(val items: List<T>, val limit: Int, val offset: Int, val total: Int, val next: String? = null)

@Serializable                       // RFC 9457 Problem Details; "errors" is an extension member
data class Problem(val type: String, val title: String, val status: Int, val detail: String,
                   val errors: List<String>? = null)

fun <T> paginate(all: List<T>, limit: Int, offset: Int, path: String): Page<T> {
    val items = all.drop(offset).take(limit)
    val next = if (offset + limit < all.size) "$path?limit=$limit&offset=${offset + limit}" else null
    return Page(items, limit, offset, all.size, next)
}

fun validate(b: NewBook): List<String> = buildList {      // collect ALL problems, not just the first
    if (b.title.isBlank()) add("title must not be blank")
    if (b.year !in 1450..2100) add("year must be between 1450 and 2100")
}

val json = Json { explicitNulls = false }                // omit null members

fun main() {
    val books = (1L..5L).map { Book(it, "Book $it", 2000 + it.toInt()) }
    println(json.encodeToString(paginate(books, limit = 2, offset = 0, path = "/books")))
    println(json.encodeToString(paginate(books, limit = 2, offset = 4, path = "/books")))

    val errors = validate(NewBook(title = " ", year = 3000))
    val problem = Problem("https://example.com/problems/validation", "Invalid book", 400,
                          "${errors.size} field(s) failed validation", errors)
    println(json.encodeToString(problem))
}
