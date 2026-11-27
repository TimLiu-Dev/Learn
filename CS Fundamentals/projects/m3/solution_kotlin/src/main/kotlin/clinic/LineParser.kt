package clinic

import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** One request, as written by the web server. */
class LogLine(
    val epochMs: Long,
    val timestamp: String, // the original text, e.g. 2026-03-02T00:00:00.471Z
    val user: String,      // "-" when nobody was logged in
    val method: String,
    val path: String,      // with the query string
    val status: Int,
    val latencyMs: Int,
    val bytes: Long,
)

/**
 * A hand-written parser that accepts exactly the lines the original regex accepted:
 *
 *   ^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z) (\S+) (\S+) ([A-Z]+) (/\S*) (\d{3}) (\d{1,7}) (\S+)$
 *
 * FIX (parsing): the original compiled that regex for every line and then ran
 * Instant.parse; this scans the line once with indexOf. It is differential-tested
 * against the regex in LineParserTest.
 *
 * Not thread-safe: it caches the last date it saw.
 */
class LineParser {
    private var cachedDate: String? = null
    private var cachedDayStartMs = 0L

    fun parse(line: String): LogLine? {
        // Eight fields, separated by single spaces, none empty, none containing other whitespace.
        val ends = IntArray(8)
        var start = 0
        for (f in 0 until 8) {
            val end = if (f == 7) line.length else line.indexOf(' ', start)
            if (end <= start) return null // missing field, or an empty one ("a  b", leading or trailing space)
            ends[f] = end
            start = end + 1
        }
        if (line.indexOf(' ', ends[6] + 1) >= 0) return null // a ninth field
        if (!noOtherWhitespace(line)) return null

        val tsEnd = ends[0]
        if (tsEnd != 24 || !isTimestampShape(line)) return null
        val userStart = ends[1] + 1
        val methodStart = ends[2] + 1
        val pathStart = ends[3] + 1
        val statusStart = ends[4] + 1
        val latencyStart = ends[5] + 1
        val bytesStart = ends[6] + 1

        if (!allIn(line, methodStart, ends[3], 'A', 'Z')) return null
        if (line[pathStart] != '/') return null
        if (ends[5] - statusStart != 3 || !allIn(line, statusStart, ends[5], '0', '9')) return null
        val latencyLen = ends[6] - latencyStart
        if (latencyLen > 7 || !allIn(line, latencyStart, ends[6], '0', '9')) return null

        val timestamp = line.substring(0, 24)
        val epochMs = epochMillis(line, timestamp) ?: return null
        // FIX (exceptions): "-" is in about 6% of lines; throwing and catching a
        // NumberFormatException for each was pure overhead. toLongOrNull accepts the
        // same strings as Long.parseLong.
        val bytes = line.substring(bytesStart).toLongOrNull() ?: 0L
        return LogLine(
            epochMs = epochMs,
            timestamp = timestamp,
            user = line.substring(userStart, ends[2]),
            method = line.substring(methodStart, ends[3]),
            path = line.substring(pathStart, ends[4]),
            status = digits(line, statusStart, ends[5]),
            latencyMs = digits(line, latencyStart, ends[6]),
            bytes = bytes,
        )
    }

    /**
     * FIX (Instant.parse): the date part changes only a few times per file, so it is
     * converted once and cached; the time of day is plain arithmetic. Anything unusual
     * (hour 24, second 60, an impossible date) goes to Instant.parse, so the answer is
     * always the one the original gave.
     */
    private fun epochMillis(line: String, timestamp: String): Long? {
        val hh = digits(line, 11, 13)
        val mm = digits(line, 14, 16)
        val ss = digits(line, 17, 19)
        val ms = digits(line, 20, 23)
        if (hh > 23 || mm > 59 || ss > 59) return slowPath(timestamp)
        if (cachedDate == null || !line.regionMatches(0, cachedDate!!, 0, 10)) {
            val day = try {
                LocalDate.of(digits(line, 0, 4), digits(line, 5, 7), digits(line, 8, 10))
            } catch (e: DateTimeException) {
                return slowPath(timestamp)
            }
            cachedDate = line.substring(0, 10)
            cachedDayStartMs = day.toEpochDay() * 86_400_000L
        }
        return cachedDayStartMs + ((hh * 60L + mm) * 60L + ss) * 1000L + ms
    }

    private fun slowPath(timestamp: String): Long? =
        try {
            Instant.parse(timestamp).toEpochMilli()
        } catch (e: DateTimeParseException) {
            null
        }

    private fun isTimestampShape(s: String): Boolean {
        for (i in 0 until 24) {
            val ok = when (i) {
                4, 7 -> s[i] == '-'
                10 -> s[i] == 'T'
                13, 16 -> s[i] == ':'
                19 -> s[i] == '.'
                23 -> s[i] == 'Z'
                else -> s[i] in '0'..'9'
            }
            if (!ok) return false
        }
        return true
    }

    // \S in java.util.regex means "not one of [ \t\n\x0B\f\r]"; spaces were checked by the split.
    private fun noOtherWhitespace(s: String): Boolean {
        for (c in s) {
            if (c == '\t' || c == '\u000B' || c == '\u000C' || c == '\r' || c == '\n') return false
        }
        return true
    }

    private fun allIn(s: String, from: Int, to: Int, lo: Char, hi: Char): Boolean {
        for (i in from until to) if (s[i] !in lo..hi) return false
        return true
    }

    private fun digits(s: String, from: Int, to: Int): Int {
        var v = 0
        for (i in from until to) v = v * 10 + (s[i] - '0')
        return v
    }
}

/**
 * "GET", "/api/orders/123?x=1" -> "GET /api/orders/{id}".
 *
 * FIX (regex + String.format): same result as replace(Regex("/[0-9]+(?=/|$)"), "/{id}")
 * and "%s %s".format(...), without compiling a regex or parsing a format string per line.
 */
fun endpointOf(method: String, path: String): String {
    val q = path.indexOf('?')
    val end = if (q < 0) path.length else q
    val sb = StringBuilder(method.length + 1 + end).append(method).append(' ')
    var i = 0
    while (i < end) {
        // path[i] == '/': find the end of this segment
        var j = i + 1
        while (j < end && path[j] != '/') j++
        sb.append('/')
        if (j > i + 1 && allDigits(path, i + 1, j)) sb.append("{id}")
        else sb.append(path, i + 1, j)
        i = j
    }
    return sb.toString()
}

private fun allDigits(s: String, from: Int, to: Int): Boolean {
    for (k in from until to) if (s[k] !in '0'..'9') return false
    return true
}
