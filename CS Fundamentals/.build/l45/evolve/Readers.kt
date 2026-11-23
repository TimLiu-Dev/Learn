import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

// What version 1.0 of a client app knows about a book. It was shipped months ago.
@Serializable
data class BookV1(val id: Long, val title: String, val author: String, val year: Int? = null)

val strict = Json { }                              // the kotlinx default: an unknown key is an error
val tolerant = Json { ignoreUnknownKeys = true }   // a Tolerant Reader: take what you need, ignore the rest

fun read(label: String, reader: Json, body: String) =
    try {
        println("$label  ${reader.decodeFromString<BookV1>(body)}")
    } catch (e: SerializationException) {
        println("$label  FAILED: ${e.message!!.lineSequence().first()}")
    }

fun main() {
    val v1 = """{"id":1,"title":"Dune","author":"Frank Herbert","year":1965}"""
    val added = """{"id":1,"title":"Dune","author":"Frank Herbert","year":1965,"isbn":"9780441013593"}"""
    val renamed = """{"id":1,"title":"Dune","authors":["Frank Herbert"],"year":1965}"""
    read("strict,   server v1     ", strict, v1)
    read("strict,   field added   ", strict, added)
    read("tolerant, field added   ", tolerant, added)
    read("tolerant, field renamed ", tolerant, renamed)
}
