package clinic;

import java.util.Locale;
import java.util.Map;

/**
 * Not a hot spot: the report has about 50 lines, so the original's `out += ...` cost
 * nothing measurable (the profiler never showed it). Rewritten with a StringBuilder only
 * because the Stats type changed.
 */
public class ReportWriter {

    public String render(Stats stats) {
        StringBuilder out = new StringBuilder();
        out.append("LOG REPORT\n");
        out.append("lines          : ").append(stats.totalLines()).append('\n');
        out.append("parsed         : ").append(stats.parsed()).append('\n');
        out.append("malformed      : ").append(stats.malformed()).append('\n');
        out.append("first request  : ").append(stats.firstTimestamp() == null ? "-" : stats.firstTimestamp()).append('\n');
        out.append("last request   : ").append(stats.lastTimestamp() == null ? "-" : stats.lastTimestamp()).append('\n');
        out.append("bytes served   : ").append(stats.bytes()).append('\n');

        out.append("\nSTATUS CODES\n");
        int[] counts = stats.statusCounts();
        for (int status = 0; status < counts.length; status++) {
            if (counts[status] == 0) continue;
            double percent = counts[status] * 100.0 / stats.parsed();
            out.append(String.format(Locale.ROOT, "%d %9d %6.1f%%\n", status, counts[status], percent));
        }

        out.append("\nENDPOINTS\n");
        out.append(String.format(Locale.ROOT, "%9s %6s %6s %6s %6s  %s\n", "requests", "p50", "p95", "p99", "max", "endpoint"));
        for (EndpointStats e : stats.endpoints()) {
            out.append(String.format(Locale.ROOT, "%9d %6d %6d %6d %6d  %s\n",
                    e.requests(), e.p50(), e.p95(), e.p99(), e.max(), e.endpoint()));
        }

        out.append("\nUSERS\n");
        out.append("distinct users : ").append(stats.distinctUsers()).append('\n');
        out.append("sessions       : ").append(stats.sessions()).append('\n');
        double average = stats.distinctUsers() == 0 ? 0.0 : (double) stats.sessions() / stats.distinctUsers();
        out.append(String.format(Locale.ROOT, "per user (avg) : %.2f\n", average));
        out.append("most sessions  :\n");
        for (Map.Entry<String, Integer> e : stats.topUsers()) {
            out.append(String.format(Locale.ROOT, "  %-10s %d\n", e.getKey(), e.getValue()));
        }

        out.append("\nSLOWEST REQUESTS\n");
        out.append(String.format(Locale.ROOT, "%8s %8s %6s  %s\n", "ms", "line", "status", "request"));
        for (SlowRequest r : stats.slowest()) {
            out.append(String.format(Locale.ROOT, "%8d %8d %6d  %s %s\n",
                    r.latencyMs(), r.lineNo(), r.status(), r.method(), r.path()));
        }
        return out.toString();
    }
}
