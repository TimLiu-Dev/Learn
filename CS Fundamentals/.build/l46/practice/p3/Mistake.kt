class MissingConfig(message: String) : RuntimeException(message)

class Settings(private val values: Map<String, String>, private val secrets: Set<String>) {
    operator fun get(key: String): String? = values[key]

    // Logged at startup, so it must never print a secret.
    override fun toString(): String = values.toSortedMap().entries
        .joinToString(", ") { (k, v) -> "$k=${if (k in secrets) "***" else v}" }
}

// Each key takes its value from env first, then the file, then the defaults.
// If any required key has no value anywhere, throw MissingConfig("missing config: A, B"),
// naming every missing key in sorted order, so one failed start shows the whole list.
fun loadSettings(
    env: Map<String, String>,
    file: Map<String, String>,
    defaults: Map<String, String>,
    required: Set<String>,
    secrets: Set<String>,
): Settings {
    val merged = env + file + defaults
    val missing = (required - merged.keys).sorted()
    if (missing.isNotEmpty()) throw MissingConfig("missing config: " + missing.joinToString(", "))
    return Settings(merged, secrets)
}
