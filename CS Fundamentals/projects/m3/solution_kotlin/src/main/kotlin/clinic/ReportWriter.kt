package clinic

import java.util.Locale

/**
 * Not a hot spot: the report has about 50 lines, so the original's `out += ...` cost
 * nothing measurable (the profiler never showed it). Rewritten with a StringBuilder only
 * because the Stats type changed.
 */
class ReportWriter {

    fun render(stats: Stats): String {
        val out = StringBuilder()
        out.append("LOG REPORT\n")
        out.append("lines          : ${stats.totalLines}\n")
        out.append("parsed         : ${stats.parsed}\n")
        out.append("malformed      : ${stats.malformed}\n")
        out.append("first request  : ${stats.firstTimestamp ?: "-"}\n")
        out.append("last request   : ${stats.lastTimestamp ?: "-"}\n")
        out.append("bytes served   : ${stats.bytes}\n")

        out.append("\nSTATUS CODES\n")
        for (status in stats.statusCounts.indices) {
            val count = stats.statusCounts[status]
            if (count == 0) continue
            val percent = count * 100.0 / stats.parsed
            out.append(String.format(Locale.ROOT, "%d %9d %6.1f%%\n", status, count, percent))
        }

        out.append("\nENDPOINTS\n")
        out.append(String.format(Locale.ROOT, "%9s %6s %6s %6s %6s  %s\n", "requests", "p50", "p95", "p99", "max", "endpoint"))
        for (e in stats.endpoints) {
            out.append(
                String.format(Locale.ROOT, "%9d %6d %6d %6d %6d  %s\n", e.requests, e.p50, e.p95, e.p99, e.max, e.endpoint)
            )
        }

        out.append("\nUSERS\n")
        out.append("distinct users : ${stats.distinctUsers}\n")
        out.append("sessions       : ${stats.sessions}\n")
        val average = if (stats.distinctUsers == 0) 0.0 else stats.sessions.toDouble() / stats.distinctUsers
        out.append(String.format(Locale.ROOT, "per user (avg) : %.2f\n", average))
        out.append("most sessions  :\n")
        for ((user, sessions) in stats.topUsers) {
            out.append(String.format(Locale.ROOT, "  %-10s %d\n", user, sessions))
        }

        out.append("\nSLOWEST REQUESTS\n")
        out.append(String.format(Locale.ROOT, "%8s %8s %6s  %s\n", "ms", "line", "status", "request"))
        for (r in stats.slowest) {
            out.append(
                String.format(Locale.ROOT, "%8d %8d %6d  %s %s\n", r.latencyMs, r.lineNo, r.status, r.method, r.path)
            )
        }
        return out.toString()
    }
}
