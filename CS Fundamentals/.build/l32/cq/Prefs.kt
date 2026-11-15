import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Prefs(val theme: String = "light", val fontSize: Int = 14)

fun main() {
    val p = Json.decodeFromString<Prefs>("""{"fontSize":14}""")
    println(Json.encodeToString(p))
}
