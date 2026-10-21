import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class AsLong(val id: Long)
@Serializable data class AsDouble(val id: Double)   // what a JavaScript client effectively does
@Serializable data class AsString(val id: String)

fun main() {
    val json = """{"id":9007199254740993}"""        // 2^53 + 1

    println("Long:   " + Json.decodeFromString<AsLong>(json).id)
    val d = Json.decodeFromString<AsDouble>(json)
    println("Double: " + d.id.toLong() + "  re-encoded: " + Json.encodeToString(d))
    println("String: " + Json.decodeFromString<AsString>("""{"id":"9007199254740993"}""").id)

    println("0.1 + 0.2 = " + (0.1 + 0.2))           // why money is never a JSON float
    println("10 + 20 cents = " + (10L + 20L))
}
