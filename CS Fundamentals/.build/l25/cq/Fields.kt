import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class Book(val title: String) {
    var reads = 0                          // declared in the body
    val loud get() = title.uppercase()     // getter only
}

fun main() {
    val book = Book("dune").apply { reads = 2 }
    println(Json.encodeToString(Book.serializer(), book))
}
