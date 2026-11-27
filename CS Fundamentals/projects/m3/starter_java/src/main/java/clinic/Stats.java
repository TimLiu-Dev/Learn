package clinic;

import java.util.List;
import java.util.Map;

public record Stats(
        int totalLines,
        int malformed,
        Request first,
        Request last,
        long bytes,
        Map<Integer, Integer> statusCounts,
        List<EndpointStats> endpoints,
        int distinctUsers,
        int sessions,
        List<Map.Entry<String, Integer>> topUsers,
        List<Request> slowest) {

    public int parsed() {
        return totalLines - malformed;
    }
}
