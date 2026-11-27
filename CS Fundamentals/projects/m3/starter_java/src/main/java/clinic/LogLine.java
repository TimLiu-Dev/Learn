package clinic;

import java.time.Instant;

/**
 * One request, as written by the web server.
 *
 * @param timestamp the original text, e.g. 2026-03-02T00:00:00.471Z
 * @param user      "-" when nobody was logged in
 * @param path      with the query string, e.g. /api/search?q=tea
 */
public record LogLine(
        Instant time,
        String timestamp,
        String ip,
        String user,
        String method,
        String path,
        int status,
        int latencyMs,
        long bytes) {
}
