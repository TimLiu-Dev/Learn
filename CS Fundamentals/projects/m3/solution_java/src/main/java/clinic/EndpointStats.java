package clinic;

public record EndpointStats(String endpoint, int requests, int p50, int p95, int p99, int max) {
}
