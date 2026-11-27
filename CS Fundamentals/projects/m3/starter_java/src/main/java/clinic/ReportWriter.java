package clinic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportWriter {

    public String render(Stats stats) {
        String out = "";
        out += "LOG REPORT\n";
        out += "lines          : " + stats.totalLines() + "\n";
        out += "parsed         : " + stats.parsed() + "\n";
        out += "malformed      : " + stats.malformed() + "\n";
        out += "first request  : " + (stats.first() == null ? "-" : stats.first().line().timestamp()) + "\n";
        out += "last request   : " + (stats.last() == null ? "-" : stats.last().line().timestamp()) + "\n";
        out += "bytes served   : " + stats.bytes() + "\n";

        out += "\nSTATUS CODES\n";
        List<Integer> codes = new ArrayList<>(stats.statusCounts().keySet());
        Collections.sort(codes);
        for (int status : codes) {
            int count = stats.statusCounts().get(status);
            double percent = count * 100.0 / stats.parsed();
            out += String.format(Locale.ROOT, "%d %9d %6.1f%%\n", status, count, percent);
        }

        out += "\nENDPOINTS\n";
        out += String.format(Locale.ROOT, "%9s %6s %6s %6s %6s  %s\n", "requests", "p50", "p95", "p99", "max", "endpoint");
        for (EndpointStats e : stats.endpoints()) {
            out += String.format(Locale.ROOT, "%9d %6d %6d %6d %6d  %s\n",
                    e.requests(), e.p50(), e.p95(), e.p99(), e.max(), e.endpoint());
        }

        out += "\nUSERS\n";
        out += "distinct users : " + stats.distinctUsers() + "\n";
        out += "sessions       : " + stats.sessions() + "\n";
        double average = stats.distinctUsers() == 0 ? 0.0 : (double) stats.sessions() / stats.distinctUsers();
        out += String.format(Locale.ROOT, "per user (avg) : %.2f\n", average);
        out += "most sessions  :\n";
        for (Map.Entry<String, Integer> e : stats.topUsers()) {
            out += String.format(Locale.ROOT, "  %-10s %d\n", e.getKey(), e.getValue());
        }

        out += "\nSLOWEST REQUESTS\n";
        out += String.format(Locale.ROOT, "%8s %8s %6s  %s\n", "ms", "line", "status", "request");
        for (Request r : stats.slowest()) {
            out += String.format(Locale.ROOT, "%8d %8d %6d  %s %s\n",
                    r.line().latencyMs(), r.lineNo(), r.line().status(), r.line().method(), r.line().path());
        }
        return out;
    }
}
