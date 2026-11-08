class ConfigException(message: String, cause: Throwable? = null) : Exception(message, cause)

class Conn(val name: String, private val line: String) : AutoCloseable {
    var closed = false
        private set

    fun readLine(): String {
        check(!closed) { "connection $name is closed" }
        if (line == "DROP") throw IllegalStateException("connection $name dropped")
        return line
    }

    override fun close() {
        closed = true
    }
}

fun readPort(conn: Conn): Int {
    val text = conn.readLine()
    val port = try {
        text.trim().toInt()
    } catch (e: NumberFormatException) {
        throw ConfigException("bad port in ${conn.name}: '$text'", e)
    }
    conn.close()      // only reached on the happy path
    return port
}
