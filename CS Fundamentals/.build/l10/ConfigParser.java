import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

public class ConfigParser {
    static class ConfigFormatException extends Exception {
        ConfigFormatException(int lineNo, String line) {
            super("line " + lineNo + ": missing '=' in \"" + line + "\"");
        }
    }

    sealed interface LineResult permits Entry, BadValue, Malformed, Skipped {}
    record Entry(String key, String value) implements LineResult {}
    record BadValue(String key, String reason) implements LineResult {}
    record Malformed(String message) implements LineResult {}
    record Skipped() implements LineResult {}

    static final Set<String> INT_KEYS = Set.of("port", "timeout", "retries");

    // Low level: splitting a line either works or the line is structurally broken.
    static String[] splitEntry(int lineNo, String line) throws ConfigFormatException {
        int eq = line.indexOf('=');
        if (eq < 0) throw new ConfigFormatException(lineNo, line);
        return new String[] { line.substring(0, eq).trim(), line.substring(eq + 1).trim() };
    }

    static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Boundary: turn every outcome, including the exception, into a value.
    static LineResult parseLine(int lineNo, String raw) {
        String line = raw.trim();
        if (line.isEmpty() || line.startsWith("#")) return new Skipped();
        String[] kv;
        try {
            kv = splitEntry(lineNo, line);
        } catch (ConfigFormatException e) {
            return new Malformed(e.getMessage());
        }
        String key = kv[0], value = kv[1];
        if (value.isEmpty()) return new BadValue(key, "value is empty");
        if (INT_KEYS.contains(key) && !isInt(value)) {
            return new BadValue(key, "'" + value + "' is not a whole number");
        }
        return new Entry(key, value);
    }

    public static void main(String[] args) throws IOException {   // readLine() declares IOException
        String text = """
            # server config
            port=8080
            timeout=abc
            host=
            garbage
            retries = 3""";

        int ok = 0, bad = 0, malformed = 0, skipped = 0;
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            int lineNo = 0;
            String raw;
            while ((raw = reader.readLine()) != null) {
                lineNo++;
                switch (parseLine(lineNo, raw)) {
                    case Entry e -> { ok++; System.out.println("line " + lineNo + ": " + e.key() + " = " + e.value()); }
                    case BadValue b -> { bad++; System.out.println("line " + lineNo + ": " + b.key() + ": " + b.reason()); }
                    case Malformed m -> { malformed++; System.out.println(m.message()); }
                    case Skipped s -> { skipped++; System.out.println("line " + lineNo + ": skipped"); }
                }
            }
        }
        System.out.println("summary: " + ok + " ok, " + bad + " bad values, " + malformed + " malformed, " + skipped + " skipped");
    }
}
