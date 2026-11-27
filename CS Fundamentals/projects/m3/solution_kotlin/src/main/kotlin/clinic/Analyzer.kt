package clinic

import java.util.PriorityQueue

/** A new session starts when a user has been quiet for more than 30 minutes. */
const val SESSION_GAP_MS = 30 * 60 * 1000L

class EndpointStats(
    val endpoint: String,
    val requests: Int,
    val p50: Int,
    val p95: Int,
    val p99: Int,
    val max: Int,
)

class SlowRequest(val lineNo: Int, val latencyMs: Int, val status: Int, val method: String, val path: String)

class Stats(
    val totalLines: Int,
    val malformed: Int,
    val firstTimestamp: String?,
    val lastTimestamp: String?,
    val bytes: Long,
    val statusCounts: IntArray, // index = status code (always three digits)
    val endpoints: List<EndpointStats>,
    val distinctUsers: Int,
    val sessions: Int,
    val topUsers: List<Pair<String, Int>>,
    val slowest: List<SlowRequest>,
) {
    val parsed: Int get() = totalLines - malformed
}

/**
 * Builds the report in one pass, keeping only small running totals.
 *
 * FIX (memory): the original kept every line, every parsed request and a static
 * ParseCache of all lines ever parsed (a leak: it was never cleared). Here a request is
 * forgotten as soon as it has been counted, so the heap needed no longer grows with the
 * file size, apart from one Int per request for the percentiles.
 */
class Analyzer {
    private val parser = LineParser()
    private var totalLines = 0
    private var malformed = 0
    private var firstMs = Long.MAX_VALUE
    private var firstTimestamp: String? = null
    private var lastMs = Long.MIN_VALUE
    private var lastTimestamp: String? = null
    private var bytes = 0L

    // FIX (boxing): an IntArray indexed by status code instead of HashMap<Int, Int>.
    private val statusCounts = IntArray(1000)
    private val endpoints = HashMap<String, Latencies>()

    // FIX (quadratic lookups + LinkedList): users were found with List.contains (a scan of
    // up to 3,000 names per request) and sessions were counted with LinkedList.get(i),
    // which walks the list from one end for every i. One hash lookup per request now
    // updates the user's last-seen time and session count.
    private val users = HashMap<String, UserSessions>()

    // FIX (sorting everything): top 10 with a size-10 heap instead of sorting all requests.
    // The root is the request that would drop out first: the fastest, and among equally
    // fast ones the latest line (the original's stable sort kept earlier lines first).
    private val slowest = PriorityQueue<SlowRequest>(
        compareBy<SlowRequest> { it.latencyMs }.thenByDescending { it.lineNo }
    )

    fun add(line: String) {
        totalLines++
        val r = parser.parse(line)
        if (r == null) {
            malformed++
            return
        }
        if (r.epochMs < firstMs) {
            firstMs = r.epochMs
            firstTimestamp = r.timestamp
        }
        if (r.epochMs > lastMs) {
            lastMs = r.epochMs
            lastTimestamp = r.timestamp
        }
        bytes += r.bytes
        statusCounts[r.status]++
        endpoints.getOrPut(endpointOf(r.method, r.path)) { Latencies() }.add(r.latencyMs)
        if (r.user != "-") {
            users.getOrPut(r.user) { UserSessions() }.seen(r.epochMs)
        }
        offerSlow(SlowRequest(totalLines, r.latencyMs, r.status, r.method, r.path))
    }

    private fun offerSlow(s: SlowRequest) {
        if (slowest.size < 10) {
            slowest.add(s)
        } else if (slowest.comparator().compare(s, slowest.peek()) > 0) {
            slowest.poll()
            slowest.add(s)
        }
    }

    fun finish(): Stats {
        val endpointStats = endpoints.map { (endpoint, l) -> l.summarise(endpoint) }
            .sortedWith(compareByDescending<EndpointStats> { it.requests }.thenBy { it.endpoint })
        val topUsers = users.entries
            .sortedWith(compareByDescending<Map.Entry<String, UserSessions>> { it.value.sessions }.thenBy { it.key })
            .take(5)
            .map { it.key to it.value.sessions }
        val slowestFirst = slowest.sortedWith(
            compareByDescending<SlowRequest> { it.latencyMs }.thenBy { it.lineNo }
        )
        return Stats(
            totalLines, malformed, firstTimestamp, lastTimestamp, bytes, statusCounts,
            endpointStats, users.size, users.values.sumOf { it.sessions }, topUsers, slowestFirst,
        )
    }
}

/** Sessions of one user, looking at their requests in file order (not sorted by time). */
private class UserSessions {
    var sessions = 0
    private var lastMs = 0L

    fun seen(ms: Long) {
        if (sessions == 0 || ms - lastMs > SESSION_GAP_MS) sessions++
        lastMs = ms
    }
}

/** A growable IntArray: 4 bytes per latency instead of a boxed Integer in a list. */
private class Latencies {
    private var values = IntArray(64)
    private var size = 0

    fun add(v: Int) {
        if (size == values.size) values = values.copyOf(size * 2)
        values[size++] = v
    }

    // FIX (percentiles): sort once per endpoint and read three ranks, instead of filtering
    // all requests per endpoint and sorting a boxed copy for each percentile.
    fun summarise(endpoint: String): EndpointStats {
        val sorted = values.copyOf(size)
        sorted.sort()
        return EndpointStats(
            endpoint, size, percentile(sorted, 50), percentile(sorted, 95), percentile(sorted, 99), sorted[size - 1],
        )
    }
}

/** Nearest-rank percentile of an ascending array: the value at rank ceil(p/100 * n). */
fun percentile(sorted: IntArray, p: Int): Int {
    val rank = (p * sorted.size + 99) / 100
    return sorted[maxOf(rank, 1) - 1]
}
