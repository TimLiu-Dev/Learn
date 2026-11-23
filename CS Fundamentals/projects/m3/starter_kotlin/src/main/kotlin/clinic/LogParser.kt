package clinic

import java.time.Instant
import java.time.format.DateTimeParseException

/** One request, as written by the web server. */
data class LogLine(
    val time: Instant,
    val timestamp: String, // the original text, e.g. 2026-03-02T00:00:00.471Z
    val ip: String,
    val user: String,      // "-" when nobody was logged in
    val method: String,
    val path: String,      // with the query string, e.g. /api/search?q=tea
    val status: Int,
    val latencyMs: Int,
    val bytes: Long,
)

object LogParser {

    /** Returns null for a line that isn't a request (truncated writes, stack traces, ...). */
    fun parse(line: String): LogLine? {
        val pattern = Regex(
            """^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z) (\S+) (\S+) ([A-Z]+) (/\S*) (\d{3}) (\d{1,7}) (\S+)$"""
        )
        val match = pattern.matchEntire(line) ?: return null
        val g = match.groupValues
        val time = try {
            Instant.parse(g[1])
        } catch (e: DateTimeParseException) {
            return null
        }
        return LogLine(
            time = time,
            timestamp = g[1],
            ip = g[2],
            user = g[3],
            method = g[4],
            path = g[5],
            status = g[6].toInt(),
            latencyMs = g[7].toInt(),
            bytes = parseBytes(g[8]),
        )
    }

    /** The server writes "-" when no body was sent; anything that isn't a number counts as 0. */
    fun parseBytes(text: String): Long =
        try {
            text.toLong()
        } catch (e: NumberFormatException) {
            0L
        }

    /** "GET /api/orders/123?x=1" -> "GET /api/orders/{id}", so all orders count as one endpoint. */
    fun endpointOf(method: String, path: String): String {
        val withoutQuery = path.substringBefore('?')
        val normalised = withoutQuery.replace(Regex("/[0-9]+(?=/|$)"), "/{id}")
        return "%s %s".format(method, normalised)
    }
}
