package clinic;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * A hand-written parser that accepts exactly the lines the original regex accepted:
 *
 *   ^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z) (\S+) (\S+) ([A-Z]+) (/\S*) (\d{3}) (\d{1,7}) (\S+)$
 *
 * FIX (parsing): the original compiled that regex for every line and then ran
 * Instant.parse; this scans the line once with indexOf. It is differential-tested
 * against the regex in LineParserTest.
 *
 * Not thread-safe: it caches the last date it saw.
 */
public final class LineParser {
    private static final long NOT_A_TIME = Long.MIN_VALUE;

    private String cachedDate;
    private long cachedDayStartMs;

    /** Returns null for a line that isn't a request. */
    public LogLine parse(String line) {
        // Eight fields, separated by single spaces, none empty, none containing other whitespace.
        int[] ends = new int[8];
        int start = 0;
        for (int f = 0; f < 8; f++) {
            int end = f == 7 ? line.length() : line.indexOf(' ', start);
            if (end <= start) return null; // missing field, or an empty one ("a  b", leading or trailing space)
            ends[f] = end;
            start = end + 1;
        }
        if (line.indexOf(' ', ends[6] + 1) >= 0) return null; // a ninth field
        if (!noOtherWhitespace(line)) return null;

        if (ends[0] != 24 || !isTimestampShape(line)) return null;
        int userStart = ends[1] + 1;
        int methodStart = ends[2] + 1;
        int pathStart = ends[3] + 1;
        int statusStart = ends[4] + 1;
        int latencyStart = ends[5] + 1;
        int bytesStart = ends[6] + 1;

        if (!allIn(line, methodStart, ends[3], 'A', 'Z')) return null;
        if (line.charAt(pathStart) != '/') return null;
        if (ends[5] - statusStart != 3 || !allIn(line, statusStart, ends[5], '0', '9')) return null;
        if (ends[6] - latencyStart > 7 || !allIn(line, latencyStart, ends[6], '0', '9')) return null;

        String timestamp = line.substring(0, 24);
        long epochMs = epochMillis(line, timestamp);
        if (epochMs == NOT_A_TIME) return null;
        return new LogLine(
                epochMs,
                timestamp,
                line.substring(userStart, ends[2]),
                line.substring(methodStart, ends[3]),
                line.substring(pathStart, ends[4]),
                digits(line, statusStart, ends[5]),
                digits(line, latencyStart, ends[6]),
                parseBytes(line, bytesStart));
    }

    /**
     * FIX (exceptions): "-" is in about 6% of lines; throwing and catching a
     * NumberFormatException for each was pure overhead. Plain digits (the normal case) and
     * "-" are handled directly; anything else still goes to Long.parseLong, so the result
     * is always the original's.
     */
    static long parseBytes(String line, int from) {
        int len = line.length() - from;
        if (len == 1 && line.charAt(from) == '-') return 0L;
        if (len <= 18 && allIn(line, from, line.length(), '0', '9')) {
            long v = 0;
            for (int i = from; i < line.length(); i++) v = v * 10 + (line.charAt(i) - '0');
            return v;
        }
        try {
            return Long.parseLong(line.substring(from));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * FIX (Instant.parse): the date part changes only a few times per file, so it is
     * converted once and cached; the time of day is plain arithmetic. Anything unusual
     * (hour 24, second 60, an impossible date) goes to Instant.parse, so the answer is
     * always the one the original gave.
     */
    private long epochMillis(String line, String timestamp) {
        int hh = digits(line, 11, 13);
        int mm = digits(line, 14, 16);
        int ss = digits(line, 17, 19);
        int ms = digits(line, 20, 23);
        if (hh > 23 || mm > 59 || ss > 59) return slowPath(timestamp);
        if (cachedDate == null || !line.regionMatches(0, cachedDate, 0, 10)) {
            LocalDate day;
            try {
                day = LocalDate.of(digits(line, 0, 4), digits(line, 5, 7), digits(line, 8, 10));
            } catch (DateTimeException e) {
                return slowPath(timestamp);
            }
            cachedDate = line.substring(0, 10);
            cachedDayStartMs = day.toEpochDay() * 86_400_000L;
        }
        return cachedDayStartMs + ((hh * 60L + mm) * 60L + ss) * 1000L + ms;
    }

    private static long slowPath(String timestamp) {
        try {
            return Instant.parse(timestamp).toEpochMilli();
        } catch (DateTimeParseException e) {
            return NOT_A_TIME;
        }
    }

    private static boolean isTimestampShape(String s) {
        for (int i = 0; i < 24; i++) {
            char c = s.charAt(i);
            boolean ok = switch (i) {
                case 4, 7 -> c == '-';
                case 10 -> c == 'T';
                case 13, 16 -> c == ':';
                case 19 -> c == '.';
                case 23 -> c == 'Z';
                default -> c >= '0' && c <= '9';
            };
            if (!ok) return false;
        }
        return true;
    }

    // \S in java.util.regex means "not one of [ \t\n\x0B\f\r]"; spaces were checked by the split.
    private static boolean noOtherWhitespace(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\t' || c == '\u000B' || c == '\f' || c == '\r' || c == '\n') return false;
        }
        return true;
    }

    static boolean allIn(String s, int from, int to, char lo, char hi) {
        for (int i = from; i < to; i++) {
            char c = s.charAt(i);
            if (c < lo || c > hi) return false;
        }
        return true;
    }

    private static int digits(String s, int from, int to) {
        int v = 0;
        for (int i = from; i < to; i++) v = v * 10 + (s.charAt(i) - '0');
        return v;
    }
}
