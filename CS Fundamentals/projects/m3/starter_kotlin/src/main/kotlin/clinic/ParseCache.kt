package clinic

/**
 * Access logs repeat themselves a lot (health checks, retries, crawlers),
 * so we remember every line we have parsed and skip the regex next time.
 */
object ParseCache {
    private val parsed = HashMap<String, LogLine?>()

    fun parse(line: String): LogLine? {
        if (parsed.containsKey(line)) return parsed[line]
        val result = LogParser.parse(line)
        parsed[line] = result
        return result
    }

    val size: Int get() = parsed.size
}
