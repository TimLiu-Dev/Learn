import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable data class User(val id: Int, val name: String)

val lenient = Json { ignoreUnknownKeys = true }

fun attempt(label: String, json: Json, text: String) {
    try {
        println("$label -> ${json.decodeFromString<User>(text)}")
    } catch (e: SerializationException) {
        println("$label -> ${e::class.simpleName}: ${e.message?.lines()?.first()}")
    }
}

fun main() {
    attempt("missing", Json, """{"id":1}""")
    attempt("wrong type", Json, """{"id":"one","name":"Ada"}""")
    attempt("unknown key", Json, """{"id":1,"name":"Ada","admin":true}""")
    attempt("unknown, lenient", lenient, """{"id":1,"name":"Ada","admin":true}""")
}
