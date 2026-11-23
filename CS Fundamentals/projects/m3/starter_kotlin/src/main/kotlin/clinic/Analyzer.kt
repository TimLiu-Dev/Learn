package clinic

import java.util.LinkedList

/** A new session starts when a user has been quiet for more than 30 minutes. */
const val SESSION_GAP_MS = 30 * 60 * 1000L

/** A parsed request plus where it was in the file (1-based, counting every line). */
data class Request(val lineNo: Int, val line: LogLine) {
    val endpoint: String = LogParser.endpointOf(line.method, line.path)
}

data class EndpointStats(
    val endpoint: String,
    val requests: Int,
    val p50: Int,
    val p95: Int,
    val p99: Int,
    val max: Int,
)

data class Stats(
    val totalLines: Int,
    val malformed: Int,
    val first: Request?,
    val last: Request?,
    val bytes: Long,
    val statusCounts: Map<Int, Int>,
    val endpoints: List<EndpointStats>,
    val distinctUsers: Int,
    val sessions: Int,
    val topUsers: List<Pair<String, Int>>,
    val slowest: List<Request>,
) {
    val parsed: Int get() = totalLines - malformed
}

class Analyzer {

    fun analyze(lines: List<String>): Stats {
        // LinkedList: we only ever append, and appending to a LinkedList never copies.
        val requests = LinkedList<Request>()
        var malformed = 0
        var lineNo = 0
        for (line in lines) {
            lineNo++
            val parsed = ParseCache.parse(line)
            if (parsed == null) {
                malformed++
                continue
            }
            requests.add(Request(lineNo, parsed))
        }

        return Stats(
            totalLines = lines.size,
            malformed = malformed,
            first = requests.minByOrNull { it.line.time },
            last = requests.maxByOrNull { it.line.time },
            bytes = requests.sumOf { it.line.bytes },
            statusCounts = countStatuses(requests),
            endpoints = endpointStats(requests),
            distinctUsers = distinctUsers(requests).size,
            sessions = sessionsByUser(requests).values.sum(),
            topUsers = topUsers(sessionsByUser(requests)),
            slowest = requests.sortedByDescending { it.line.latencyMs }.take(10),
        )
    }

    private fun countStatuses(requests: List<Request>): Map<Int, Int> {
        val counts = HashMap<Int, Int>()
        for (r in requests) {
            counts[r.line.status] = (counts[r.line.status] ?: 0) + 1
        }
        return counts
    }

    private fun endpointStats(requests: List<Request>): List<EndpointStats> {
        val endpoints = ArrayList<String>()
        for (r in requests) {
            if (!endpoints.contains(r.endpoint)) endpoints.add(r.endpoint)
        }
        return endpoints.map { endpoint ->
            val latencies = requests.filter { it.endpoint == endpoint }.map { it.line.latencyMs }
            EndpointStats(
                endpoint = endpoint,
                requests = latencies.size,
                p50 = percentile(latencies, 50),
                p95 = percentile(latencies, 95),
                p99 = percentile(latencies, 99),
                max = latencies.max(),
            )
        }.sortedWith(compareByDescending<EndpointStats> { it.requests }.thenBy { it.endpoint })
    }

    private fun distinctUsers(requests: List<Request>): List<String> {
        val users = ArrayList<String>()
        for (r in requests) {
            val user = r.line.user
            if (user != "-" && !users.contains(user)) users.add(user)
        }
        return users
    }

    /** Sessions per user, looking at each user's requests in file order. */
    private fun sessionsByUser(requests: List<Request>): Map<String, Int> {
        val times = HashMap<String, LinkedList<Long>>()
        for (r in requests) {
            if (r.line.user == "-") continue
            times.getOrPut(r.line.user) { LinkedList() }.add(r.line.time.toEpochMilli())
        }
        val sessions = HashMap<String, Int>()
        for (user in distinctUsers(requests)) {
            val userTimes = times.getValue(user)
            var count = 1
            for (i in 1 until userTimes.size) {
                if (userTimes[i] - userTimes[i - 1] > SESSION_GAP_MS) count++
            }
            sessions[user] = count
        }
        return sessions
    }

    private fun topUsers(sessions: Map<String, Int>): List<Pair<String, Int>> =
        sessions.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(5)
            .map { it.key to it.value }
}

/** Nearest-rank percentile: the smallest value with at least p% of the values at or below it. */
fun percentile(values: List<Int>, p: Int): Int {
    val sorted = values.sorted()
    val rank = (p * sorted.size + 99) / 100 // ceil(p/100 * n) without floating point
    return sorted[maxOf(rank, 1) - 1]
}
