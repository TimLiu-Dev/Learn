class MissingConfig(message: String) : RuntimeException(message)

class Settings(private val values: Map<String, String>, private val secrets: Set<String>) {
    operator fun get(key: String): String? = values[key]

    // "KEY=value" pairs sorted by key and joined with ", ", with every secret shown as ***.
    override fun toString(): String = TODO("sort, mask the secrets, join")
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
): Settings = TODO("merge the three layers, then check the required keys")
