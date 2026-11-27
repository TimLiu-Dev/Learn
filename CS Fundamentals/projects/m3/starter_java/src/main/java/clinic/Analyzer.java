package clinic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Analyzer {
    /** A new session starts when a user has been quiet for more than 30 minutes. */
    public static final long SESSION_GAP_MS = 30 * 60 * 1000L;

    public Stats analyze(List<String> lines) {
        // LinkedList: we only ever append, and appending to a LinkedList never copies.
        List<Request> requests = new LinkedList<>();
        int malformed = 0;
        int lineNo = 0;
        for (String line : lines) {
            lineNo++;
            LogLine parsed = ParseCache.parse(line);
            if (parsed == null) {
                malformed++;
                continue;
            }
            requests.add(new Request(lineNo, parsed));
        }

        Request first = null;
        Request last = null;
        long bytes = 0;
        for (Request r : requests) {
            if (first == null || r.line().time().isBefore(first.line().time())) first = r;
            if (last == null || r.line().time().isAfter(last.line().time())) last = r;
            bytes += r.line().bytes();
        }

        List<Request> bySlowest = new ArrayList<>(requests);
        bySlowest.sort(Comparator.comparingInt((Request r) -> r.line().latencyMs()).reversed());

        return new Stats(
                lines.size(),
                malformed,
                first,
                last,
                bytes,
                countStatuses(requests),
                endpointStats(requests),
                distinctUsers(requests).size(),
                sessionsByUser(requests).values().stream().mapToInt(Integer::intValue).sum(),
                topUsers(sessionsByUser(requests)),
                bySlowest.subList(0, Math.min(10, bySlowest.size())));
    }

    private Map<Integer, Integer> countStatuses(List<Request> requests) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Request r : requests) {
            counts.merge(r.line().status(), 1, Integer::sum);
        }
        return counts;
    }

    private List<EndpointStats> endpointStats(List<Request> requests) {
        List<String> endpoints = new ArrayList<>();
        for (Request r : requests) {
            if (!endpoints.contains(r.endpoint())) endpoints.add(r.endpoint());
        }
        List<EndpointStats> result = new ArrayList<>();
        for (String endpoint : endpoints) {
            List<Integer> latencies = new ArrayList<>();
            for (Request r : requests) {
                if (r.endpoint().equals(endpoint)) latencies.add(r.line().latencyMs());
            }
            int max = 0;
            for (int l : latencies) max = Math.max(max, l);
            result.add(new EndpointStats(
                    endpoint,
                    latencies.size(),
                    percentile(latencies, 50),
                    percentile(latencies, 95),
                    percentile(latencies, 99),
                    max));
        }
        result.sort(Comparator.comparingInt(EndpointStats::requests).reversed()
                .thenComparing(EndpointStats::endpoint));
        return result;
    }

    private List<String> distinctUsers(List<Request> requests) {
        List<String> users = new ArrayList<>();
        for (Request r : requests) {
            String user = r.line().user();
            if (!user.equals("-") && !users.contains(user)) users.add(user);
        }
        return users;
    }

    /** Sessions per user, looking at each user's requests in file order. */
    private Map<String, Integer> sessionsByUser(List<Request> requests) {
        Map<String, List<Long>> times = new HashMap<>();
        for (Request r : requests) {
            if (r.line().user().equals("-")) continue;
            times.computeIfAbsent(r.line().user(), u -> new LinkedList<>()).add(r.line().time().toEpochMilli());
        }
        Map<String, Integer> sessions = new HashMap<>();
        for (String user : distinctUsers(requests)) {
            List<Long> userTimes = times.get(user);
            int count = 1;
            for (int i = 1; i < userTimes.size(); i++) {
                if (userTimes.get(i) - userTimes.get(i - 1) > SESSION_GAP_MS) count++;
            }
            sessions.put(user, count);
        }
        return sessions;
    }

    private List<Map.Entry<String, Integer>> topUsers(Map<String, Integer> sessions) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(sessions.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));
        return entries.subList(0, Math.min(5, entries.size()));
    }

    /** Nearest-rank percentile: the smallest value with at least p% of the values at or below it. */
    static int percentile(List<Integer> values, int p) {
        List<Integer> sorted = new ArrayList<>(values);
        sorted.sort(null);
        int rank = (p * sorted.size() + 99) / 100; // ceil(p/100 * n) without floating point
        return sorted.get(Math.max(rank, 1) - 1);
    }
}
