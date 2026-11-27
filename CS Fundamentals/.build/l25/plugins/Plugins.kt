import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json

annotation class Service                 // our own marker for the all-open plugin

@Serializable
data class Book(val title: String, val pages: Int)

@Service
class Catalog {                          // no `open` keyword anywhere
    fun find(title: String) = Book(title, 412)
}

class Helper                             // not annotated

fun main() {
    val book = Catalog().find("Dune")
    println(Json.encodeToString(Book.serializer(), book))   // serializer() written by the plugin
    println(Book.serializer().descriptor.elementNames.toList())
}
