package clinic;

import java.util.List;
import java.util.Map;

/** @param statusCounts index = status code (always three digits) */
public record Stats(
        int totalLines,
        int malformed,
        String firstTimestamp,
        String lastTimestamp,
        long bytes,
        int[] statusCounts,
        List<EndpointStats> endpoints,
        int distinctUsers,
        int sessions,
        List<Map.Entry<String, Integer>> topUsers,
        List<SlowRequest> slowest) {

    public int parsed() {
        return totalLines - malformed;
    }
}
