data class ShelfConfig(val db: String, val port: Int, val poolSize: Int)

/** Spring's relaxed binding for environment variables: "shelf.pool-size" -> "SHELF_POOLSIZE". */
fun envName(property: String): String =
    property.replace('.', '_').replace("-", "").uppercase()

/** Reads each setting from env (under its relaxed name), else props, else the default. */
fun bind(props: Map<String, String>, env: Map<String, String>): ShelfConfig {
    val problems = mutableListOf<String>()

    fun raw(name: String): String? = env[envName(name)] ?: props[name]

    fun int(name: String, default: Int, range: IntRange): Int {
        val text = raw(name) ?: return default
        val n = text.toIntOrNull()
        when {
            n == null -> problems += "$name: '$text' is not a number"
            n !in range -> problems += "$name: $n is out of range $range"
            else -> return n
        }
        return default   // a placeholder; we throw below before anyone sees it
    }

    val config = ShelfConfig(
        db = raw("shelf.db") ?: "shelf.db",
        port = int("shelf.port", 8080, 0..65535),
        poolSize = int("shelf.pool-size", 4, 1..64),
    )
    // Fail once, with everything wrong, so one restart fixes the whole file.
    if (problems.isNotEmpty()) throw IllegalArgumentException(problems.joinToString("; "))
    return config
}
