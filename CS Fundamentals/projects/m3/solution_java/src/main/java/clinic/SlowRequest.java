package clinic;

public record SlowRequest(int lineNo, int latencyMs, int status, String method, String path) {
}
