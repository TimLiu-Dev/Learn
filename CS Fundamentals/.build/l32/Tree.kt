import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val response = """
  {"city": "Oslo", "current": {"tempC": 7.5, "wind": {"kmh": 18}},
   "alerts": [{"level": "yellow", "text": "Ice"}, {"level": "orange", "text": "Storm"}]}
"""

fun main() {
    val root = Json.parseToJsonElement(response).jsonObject      // no DTO classes at all
    println("city: " + root["city"]?.jsonPrimitive?.content)
    println("wind: " + root["current"]?.jsonObject?.get("wind")?.jsonObject?.get("kmh")?.jsonPrimitive?.int)
    val levels = root["alerts"]?.jsonArray?.map { it.jsonObject["level"]?.jsonPrimitive?.content }
    println("alerts: $levels")
    println("missing: " + root["humidity"])                       // absent key -> null
}
