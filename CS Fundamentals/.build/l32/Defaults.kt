import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Settings(
    val email: String?,              // nullable, no default: still REQUIRED in the JSON
    val theme: String = "light",     // has a default: optional in the JSON
    val fontSize: Int = 14,
    val nickname: String? = null,
)

val withDefaults = Json { encodeDefaults = true }    // build each configured Json once, reuse it
val withoutNulls = Json { explicitNulls = false }

fun main() {
    val s = Settings(email = null, theme = "dark")
    println(Json.encodeToString(s))                   // defaults skipped
    println(withDefaults.encodeToString(s))           // everything
    println(withoutNulls.encodeToString(s))           // nulls skipped too

    val parsed = Json.decodeFromString<Settings>("""{"email":"ada@example.com"}""")
    println(parsed)                                                   // missing keys -> defaults
}
