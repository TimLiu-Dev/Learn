import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface Event

@Serializable @SerialName("borrowed")
data class Borrowed(@SerialName("book_id") val bookId: Long, val member: String) : Event

@Serializable @SerialName("returned")
data class Returned(@SerialName("book_id") val bookId: Long, val lateDays: Int = 0) : Event

fun main() {
    val events: List<Event> = listOf(Borrowed(7, "ada"), Returned(7, lateDays = 2))
    val text = Json.encodeToString(events)
    println(text)

    val back = Json.decodeFromString<List<Event>>(text)
    for (e in back) {
        val line = when (e) {                       // exhaustive: sealed (L7)
            is Borrowed -> "${e.member} borrowed #${e.bookId}"
            is Returned -> "#${e.bookId} back, ${e.lateDays} days late"
        }
        println(line)
    }
}
