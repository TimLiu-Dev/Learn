package clinic;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Builds the report in one pass, keeping only small running totals.
 *
 * FIX (memory): the original kept every line, every parsed request and a static
 * ParseCache of all lines ever parsed (a leak: it was never cleared). Here a request is
 * forgotten as soon as it has been counted, so the heap needed no longer grows with the
 * file size, apart from one int per request for the percentiles.
 */
public class Analyzer {
    /** A new session starts when a user has been quiet for more than 30 minutes. */
    public static final long SESSION_GAP_MS = 30 * 60 * 1000L;

    private static final Comparator<SlowRequest> DROPS_OUT_FIRST =
            Comparator.comparingInt(SlowRequest::latencyMs)
                    .thenComparing(Comparator.comparingInt(SlowRequest::lineNo).reversed());

    private final LineParser parser = new LineParser();
    private int totalLines;
    private int malformed;
    private long firstMs = Long.MAX_VALUE;
    private String firstTimestamp;
    private long lastMs = Long.MIN_VALUE;
    private String lastTimestamp;
    private long bytes;

    // FIX (boxing): an int[] indexed by status code instead of Map<Integer, Integer>.merge.
    private final int[] statusCounts = new int[1000];
    private final Map<String, Latencies> endpoints = new HashMap<>();

    // FIX (quadratic lookups + LinkedList): users were found with List.contains (a scan of
    // up to 3,000 names per request) and sessions were counted with LinkedList.get(i),
    // which walks the list from one end for every i. One hash lookup per request now
    // updates the user's last-seen time and session count.
    private final Map<String, UserSessions> users = new HashMap<>();

    // FIX (sorting everything): top 10 with a size-10 heap instead of sorting all requests.
    // The root is the request that would drop out first: the fastest, and among equally
    // fast ones the latest line (the original's stable sort kept earlier lines first).
    private final PriorityQueue<SlowRequest> slowest = new PriorityQueue<>(DROPS_OUT_FIRST);

    public void add(String line) {
        totalLines++;
        LogLine r = parser.parse(line);
        if (r == null) {
            malformed++;
            return;
        }
        if (r.epochMs() < firstMs) {
            firstMs = r.epochMs();
            firstTimestamp = r.timestamp();
        }
        if (r.epochMs() > lastMs) {
            lastMs = r.epochMs();
            lastTimestamp = r.timestamp();
        }
        bytes += r.bytes();
        statusCounts[r.status()]++;
        endpoints.computeIfAbsent(Endpoints.endpointOf(r.method(), r.path()), e -> new Latencies()).add(r.latencyMs());
        if (!r.user().equals("-")) {
            users.computeIfAbsent(r.user(), u -> new UserSessions()).seen(r.epochMs());
        }
        offerSlow(new SlowRequest(totalLines, r.latencyMs(), r.status(), r.method(), r.path()));
    }

    private void offerSlow(SlowRequest s) {
        if (slowest.size() < 10) {
            slowest.add(s);
        } else if (DROPS_OUT_FIRST.compare(s, slowest.peek()) > 0) {
            slowest.poll();
            slowest.add(s);
        }
    }

    public Stats finish() {
        List<EndpointStats> endpointStats = new ArrayList<>();
        endpoints.forEach((endpoint, l) -> endpointStats.add(l.summarise(endpoint)));
        endpointStats.sort(Comparator.comparingInt(EndpointStats::requests).reversed()
                .thenComparing(EndpointStats::endpoint));

        List<Map.Entry<String, Integer>> byUser = new ArrayList<>();
        int sessions = 0;
        for (Map.Entry<String, UserSessions> e : users.entrySet()) {
            byUser.add(new SimpleImmutableEntry<>(e.getKey(), e.getValue().sessions));
            sessions += e.getValue().sessions;
        }
        byUser.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));

        List<SlowRequest> slowestFirst = new ArrayList<>(slowest);
        slowestFirst.sort(DROPS_OUT_FIRST.reversed());

        return new Stats(totalLines, malformed, firstTimestamp, lastTimestamp, bytes, statusCounts,
                endpointStats, users.size(), sessions, byUser.subList(0, Math.min(5, byUser.size())), slowestFirst);
    }

    /** Sessions of one user, looking at their requests in file order (not sorted by time). */
    private static final class UserSessions {
        int sessions;
        private long lastMs;

        void seen(long ms) {
            if (sessions == 0 || ms - lastMs > SESSION_GAP_MS) sessions++;
            lastMs = ms;
        }
    }

    /** A growable int[]: 4 bytes per latency instead of a boxed Integer in a list. */
    private static final class Latencies {
        private int[] values = new int[64];
        private int size;

        void add(int v) {
            if (size == values.length) values = Arrays.copyOf(values, size * 2);
            values[size++] = v;
        }

        // FIX (percentiles): sort once per endpoint and read three ranks, instead of filtering
        // all requests per endpoint and sorting a boxed copy for each percentile.
        EndpointStats summarise(String endpoint) {
            int[] sorted = Arrays.copyOf(values, size);
            Arrays.sort(sorted);
            return new EndpointStats(endpoint, size,
                    percentile(sorted, 50), percentile(sorted, 95), percentile(sorted, 99), sorted[size - 1]);
        }
    }

    /** Nearest-rank percentile of an ascending array: the value at rank ceil(p/100 * n). */
    static int percentile(int[] sorted, int p) {
        int rank = (p * sorted.length + 99) / 100;
        return sorted[Math.max(rank, 1) - 1];
    }
}
