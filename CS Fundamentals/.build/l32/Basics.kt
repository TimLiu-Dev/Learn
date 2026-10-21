import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Book(val id: Long, val title: String, val year: Int, val tags: List<String>, val subtitle: String?)

fun main() {
    val book = Book(1, "Dune", 1965, listOf("sci-fi", "classic"), null)

    val text = Json.encodeToString(book)           // object -> JSON text
    println(text)

    val back = Json.decodeFromString<Book>(text)   // JSON text -> object
    println(back)
    println("equal after round trip: ${back == book}")

    val pretty = Json { prettyPrint = true }
    println(pretty.encodeToString(book))
}
