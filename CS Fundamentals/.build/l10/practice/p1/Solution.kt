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

fun readPort(conn: Conn): Int = conn.use {
    val text = it.readLine()
    try {
        text.trim().toInt()
    } catch (e: NumberFormatException) {
        // translate to this level's exception, and keep the original as the cause
        throw ConfigException("bad port in ${conn.name}: '$text'", e)
    }
}
