data class ShelfConfig(val db: String, val port: Int, val poolSize: Int)

fun envName(property: String): String =
    property.replace('.', '_').replace("-", "").uppercase()

fun bind(props: Map<String, String>, env: Map<String, String>): ShelfConfig {
    fun raw(name: String): String? = env[envName(name)] ?: props[name]

    // Throws at the first problem, so a file with two mistakes needs two restarts.
    fun int(name: String, default: Int, range: IntRange): Int {
        val text = raw(name) ?: return default
        val n = text.toIntOrNull() ?: throw IllegalArgumentException("$name: '$text' is not a number")
        require(n in range) { "$name: $n is out of range $range" }
        return n
    }

    return ShelfConfig(raw("shelf.db") ?: "shelf.db", int("shelf.port", 8080, 0..65535), int("shelf.pool-size", 4, 1..64))
}
