import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Book(val id: Long, val title: String)

val tolerant = Json { ignoreUnknownKeys = true }

fun main() {
    val body = """{"id":1,"title":"Dune","isbn":"9780441013593"}"""
    val strict = runCatching { Json.decodeFromString<Book>(body) }
    val lenient = runCatching { tolerant.decodeFromString<Book>(body) }
    println("${strict.isSuccess} ${lenient.getOrNull()?.title}")
}
