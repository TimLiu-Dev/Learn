import kotlinx.serialization.json.Json

data class Plain(val id: Int)                        // no @Serializable

fun main() {
    println(Json.encodeToString(Plain(1)))
}
