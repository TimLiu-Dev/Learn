package clinic;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogParser {
    private LogParser() {
    }

    /** Returns null for a line that isn't a request (truncated writes, stack traces, ...). */
    public static LogLine parse(String line) {
        Pattern pattern = Pattern.compile(
                "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z) (\\S+) (\\S+) ([A-Z]+) (/\\S*) (\\d{3}) (\\d{1,7}) (\\S+)$");
        Matcher m = pattern.matcher(line);
        if (!m.matches()) return null;
        Instant time;
        try {
            time = Instant.parse(m.group(1));
        } catch (DateTimeParseException e) {
            return null;
        }
        return new LogLine(
                time,
                m.group(1),
                m.group(2),
                m.group(3),
                m.group(4),
                m.group(5),
                Integer.parseInt(m.group(6)),
                Integer.parseInt(m.group(7)),
                parseBytes(m.group(8)));
    }

    /** The server writes "-" when no body was sent; anything that isn't a number counts as 0. */
    public static long parseBytes(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /** "GET /api/orders/123?x=1" -> "GET /api/orders/{id}", so all orders count as one endpoint. */
    public static String endpointOf(String method, String path) {
        int q = path.indexOf('?');
        String withoutQuery = q < 0 ? path : path.substring(0, q);
        String normalised = withoutQuery.replaceAll("/[0-9]+(?=/|$)", "/{id}");
        return String.format("%s %s", method, normalised);
    }
}
