// L32's books handler, shrunk to its essentials: no HTTP server, no JSON library.
// A "request" is (method, path, body); the body is "title,year".

// ---------- Before: one class, four reasons to change ----------
class BooksHandlerBefore {
    private val books = mutableMapOf<Long, Pair<String, Int>>()
    private var nextId = 1L

    fun handle(method: String, path: String, body: String = ""): String {
        if (method == "POST" && path == "/books") {
            val parts = body.split(",")                                    // 1. the wire format
            val title = parts.getOrNull(0)?.trim().orEmpty()
            val year = parts.getOrNull(1)?.trim()?.toIntOrNull()
            if (title.isBlank()) return "400 title must not be blank"      // 2. the business rules
            if (year == null || year !in 1450..2100) return "400 year must be 1450..2100"
            val id = nextId++                                              // 3. storage
            books[id] = title to year
            return "201 {\"id\":$id,\"title\":\"$title\",\"year\":$year}"  // 4. the output format
        }
        if (method == "GET" && path.startsWith("/books/")) {
            val id = path.removePrefix("/books/").toLongOrNull() ?: return "404"
            val (title, year) = books[id] ?: return "404"
            return "200 {\"id\":$id,\"title\":\"$title\",\"year\":$year}"
        }
        return "404"
    }
}

// ---------- After: one reason to change each ----------
data class Book(val id: Long, val title: String, val year: Int)
data class NewBook(val title: String, val year: Int?)

object BookParser {                                       // changes when the wire format changes
    fun parse(body: String): NewBook {
        val parts = body.split(",")
        return NewBook(parts.getOrNull(0)?.trim().orEmpty(), parts.getOrNull(1)?.trim()?.toIntOrNull())
    }
}

object BookRules {                                        // changes when the librarians change a rule
    fun problem(b: NewBook): String? = when {
        b.title.isBlank() -> "title must not be blank"
        b.year == null || b.year !in 1450..2100 -> "year must be 1450..2100"
        else -> null
    }
}

class BookStore {                                         // changes when storage moves (L36: SQLite)
    private val books = mutableMapOf<Long, Book>()
    private var nextId = 1L
    fun add(title: String, year: Int): Book = Book(nextId++, title, year).also { books[it.id] = it }
    fun find(id: Long): Book? = books[id]
}

object BookJson {                                         // changes when the API's JSON changes
    fun write(b: Book) = "{\"id\":${b.id},\"title\":\"${b.title}\",\"year\":${b.year}}"
}

class BooksHandler(private val store: BookStore) {        // changes when routes change: nothing else
    fun handle(method: String, path: String, body: String = ""): String = when {
        method == "POST" && path == "/books" -> {
            val input = BookParser.parse(body)
            BookRules.problem(input)?.let { "400 $it" }
                ?: "201 " + BookJson.write(store.add(input.title, input.year!!))
        }
        method == "GET" && path.startsWith("/books/") ->
            path.removePrefix("/books/").toLongOrNull()?.let(store::find)
                ?.let { "200 " + BookJson.write(it) } ?: "404"
        else -> "404"
    }
}

fun main() {
    val script = listOf(
        Triple("POST", "/books", "Dune, 1965"),
        Triple("POST", "/books", " , 1965"),
        Triple("POST", "/books", "Beowulf, 1000"),
        Triple("GET", "/books/1", ""),
        Triple("GET", "/books/7", ""),
    )
    val before = BooksHandlerBefore()
    val after = BooksHandler(BookStore())
    for ((m, p, b) in script) {
        val x = before.handle(m, p, b)
        val y = after.handle(m, p, b)
        println("${"$m $p".padEnd(13)} -> $y${if (x == y) "" else "   DIFFERENT: $x"}")
    }
}
