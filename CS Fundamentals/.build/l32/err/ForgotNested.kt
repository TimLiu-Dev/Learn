import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class Plain(val id: Int)                        // no @Serializable

@Serializable
data class Holder(val inner: Plain)                  // a @Serializable class using it

fun main() {
    println(Json.encodeToString(Plain(1)))
}
