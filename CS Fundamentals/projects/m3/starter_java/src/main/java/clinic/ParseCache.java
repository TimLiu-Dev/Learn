package clinic;

import java.util.HashMap;
import java.util.Map;

/**
 * Access logs repeat themselves a lot (health checks, retries, crawlers),
 * so we remember every line we have parsed and skip the regex next time.
 */
public final class ParseCache {
    private static final Map<String, LogLine> PARSED = new HashMap<>();

    private ParseCache() {
    }

    public static LogLine parse(String line) {
        if (PARSED.containsKey(line)) return PARSED.get(line);
        LogLine result = LogParser.parse(line);
        PARSED.put(line, result);
        return result;
    }

    public static int size() {
        return PARSED.size();
    }
}
